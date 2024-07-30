package com.lvwj.halo.milvus.core;

import com.alibaba.fastjson.JSONObject;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.JsonUtil;
import com.lvwj.halo.common.utils.StringPool;
import com.lvwj.halo.common.utils.StringUtil;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.RelevanceScore;
import dev.langchain4j.store.embedding.filter.Filter;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.exception.ParamException;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.QueryResultsWrapper.RowRecord;
import io.milvus.response.SearchResultsWrapper;

import java.math.BigDecimal;
import java.util.*;

import static com.lvwj.halo.milvus.core.CollectionFieldConstant.*;
import static com.lvwj.halo.milvus.core.CollectionOperationsExecutor.*;
import static com.lvwj.halo.milvus.core.Generator.*;
import static dev.langchain4j.internal.Utils.isNullOrBlank;
import static dev.langchain4j.internal.Utils.isNullOrEmpty;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

class Mapper {

    static List<Boolean> toUnDelete(int size) {
        List<Boolean> booleans = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            booleans.add(Boolean.FALSE);
        }
        return booleans;
    }

    static List<List<Float>> toVectors(List<Embedding> embeddings) {
        return embeddings.stream()
                .map(Embedding::vectorAsList)
                .collect(toList());
    }

    static List<String> toScalars(List<TextSegment> textSegments, int size) {
        return isNullOrEmpty(textSegments) ? generateEmptyScalars(size) : textSegmentsToScalars(textSegments);
    }

    static List<JSONObject> toMetadataJsons(List<TextSegment> textSegments, int size) {
        return isNullOrEmpty(textSegments) ? generateEmptyJsons(size) : textSegments.stream()
                .map(segment -> new JSONObject(segment.metadata().toMap()))
                .collect(toList());
    }

    static List<String> textSegmentsToScalars(List<TextSegment> textSegments) {
        return textSegments.stream()
                .map(TextSegment::text)
                .collect(toList());
    }

    static List<EmbeddingMatch<TextSegment>> toEmbeddingMatches(MilvusServiceClient milvusClient,
                                                                SearchResultsWrapper resultsWrapper,
                                                                String collectionName,
                                                                ConsistencyLevelEnum consistencyLevel,
                                                                boolean queryForVectorOnSearch) {
        List<EmbeddingMatch<TextSegment>> matches = new ArrayList<>();

        Map<String, Embedding> idToEmbedding = new HashMap<>();
        if (queryForVectorOnSearch) {
            try {
                List<String> rowIds = (List<String>) resultsWrapper.getFieldWrapper(ID_FIELD_NAME).getFieldData();
                idToEmbedding.putAll(queryEmbeddings(milvusClient, collectionName, rowIds, consistencyLevel));
            } catch (ParamException e) {
                // There is no way to check if the result is empty or not.
                // If the result is empty, the exception will be thrown.
            }
        }

        for (int i = 0; i < resultsWrapper.getRowRecords().size(); i++) {
            double score = resultsWrapper.getIDScore(0).get(i).getScore();
            String rowId = resultsWrapper.getIDScore(0).get(i).getStrID();
            Embedding embedding = idToEmbedding.get(rowId);
            TextSegment textSegment = toTextSegment(resultsWrapper.getRowRecords().get(i));
            EmbeddingMatch<TextSegment> embeddingMatch = new EmbeddingMatch<>(
                    RelevanceScore.fromCosineSimilarity(score),
                    rowId,
                    embedding,
                    textSegment
            );
            matches.add(embeddingMatch);
        }

        return matches;
    }

    private static TextSegment toTextSegment(RowRecord rowRecord) {

        String text = (String) rowRecord.get(TEXT_FIELD_NAME);
        if (isNullOrBlank(text)) {
            return null;
        }

        if (!rowRecord.getFieldValues().containsKey(METADATA_FIELD_NAME)) {
            return TextSegment.from(text);
        }

        JSONObject metadata = (JSONObject) rowRecord.get(METADATA_FIELD_NAME);
        return TextSegment.from(text, toMetadata(metadata));
    }

    private static Metadata toMetadata(JSONObject metadata) {
        Map<String, Object> metadataMap = metadata.getInnerMap();
        metadataMap.forEach((key, value) -> {
            if (value instanceof BigDecimal) {
                // It is safe to convert. No information is lost, the "biggest" type allowed in Metadata is double.
                metadataMap.put(key, ((BigDecimal) value).doubleValue());
            }
        });
        return Metadata.from(metadataMap);
    }

    private static Map<String, Embedding> queryEmbeddings(MilvusServiceClient milvusClient,
                                                          String collectionName,
                                                          List<String> rowIds,
                                                          ConsistencyLevelEnum consistencyLevel) {
        QueryResultsWrapper queryResultsWrapper = queryByIds(
                milvusClient,
                collectionName,
                rowIds,
                singletonList(VECTOR_FIELD_NAME),
                consistencyLevel
        );

        Map<String, Embedding> idToEmbedding = new HashMap<>();
        for (RowRecord row : queryResultsWrapper.getRowRecords()) {
            String id = row.get(ID_FIELD_NAME).toString();
            List<Float> vector = (List<Float>) row.get(VECTOR_FIELD_NAME);
            idToEmbedding.put(id, Embedding.from(vector));
        }

        return idToEmbedding;
    }

    static Map<String, TextEmbeddingEntity> queryEntities(MilvusServiceClient milvusClient,
                                                          String collectionName,
                                                          String partitionKey,
                                                          List<String> rowIds) {
        QueryResultsWrapper queryResultsWrapper = queryByIds(
                milvusClient,
                collectionName,
                rowIds,
                Arrays.asList(ID_FIELD_NAME, VECTOR_FIELD_NAME, TEXT_FIELD_NAME, METADATA_FIELD_NAME, DELETE_FIELD_NAME, partitionKey),
                ConsistencyLevelEnum.BOUNDED
        );

        Map<String, TextEmbeddingEntity> map = new HashMap<>();
        for (RowRecord row : queryResultsWrapper.getRowRecords()) {
            String id = row.get(ID_FIELD_NAME).toString();
            List<Float> vector = (List<Float>) row.get(VECTOR_FIELD_NAME);
            String text = row.get(TEXT_FIELD_NAME).toString();
            String pKey = StringPool.EMPTY;
            if (StringUtil.isNotBlank(partitionKey)) {
                pKey = row.get(partitionKey).toString();
            }
            Boolean deleted = Func.toBoolean(row.get(DELETE_FIELD_NAME));
            Map<String, Object> metadata = JsonUtil.toMap(row.get(METADATA_FIELD_NAME).toString());
            TextSegment textSegment = null != metadata ? TextSegment.from(text, Metadata.from(metadata)) : TextSegment.from(text);
            TextEmbeddingEntity entity = TextEmbeddingEntity.from(id, Embedding.from(vector), textSegment, pKey, deleted);
            map.put(id, entity);
        }

        return map;
    }

    static List<TextEmbeddingEntity> queryEntities(MilvusServiceClient milvusClient,
                                                   String collectionName,
                                                   String partitionKey,
                                                   Filter filter) {
        QueryResultsWrapper queryResultsWrapper = queryByFilter(
                milvusClient,
                collectionName,
                partitionKey,
                filter,
                Arrays.asList(ID_FIELD_NAME, VECTOR_FIELD_NAME, TEXT_FIELD_NAME, METADATA_FIELD_NAME, DELETE_FIELD_NAME, partitionKey),
                ConsistencyLevelEnum.BOUNDED
        );

        List<TextEmbeddingEntity> list = new ArrayList<>();
        for (RowRecord row : queryResultsWrapper.getRowRecords()) {
            String id = row.get(ID_FIELD_NAME).toString();
            List<Float> vector = (List<Float>) row.get(VECTOR_FIELD_NAME);
            String text = row.get(TEXT_FIELD_NAME).toString();
            String pKey = StringPool.EMPTY;
            if (StringUtil.isNotBlank(partitionKey)) {
                pKey = row.get(partitionKey).toString();
            }
            Boolean deleted = Func.toBoolean(row.get(DELETE_FIELD_NAME));
            Map<String, Object> metadata = JsonUtil.toMap(row.get(METADATA_FIELD_NAME).toString());
            TextSegment textSegment = null != metadata ? TextSegment.from(text, Metadata.from(metadata)) : TextSegment.from(text);
            TextEmbeddingEntity entity = TextEmbeddingEntity.from(id, Embedding.from(vector), textSegment, pKey, deleted);
            list.add(entity);
        }

        return list;
    }

    static List<TextEmbeddingEntity> queryEntities(MilvusServiceClient milvusClient,
                                                   String collectionName,
                                                   String partitionKey,
                                                   String expr) {
        QueryResultsWrapper queryResultsWrapper = queryByExpr(
                milvusClient,
                collectionName,
                expr,
                Arrays.asList(ID_FIELD_NAME, VECTOR_FIELD_NAME, TEXT_FIELD_NAME, METADATA_FIELD_NAME, DELETE_FIELD_NAME, partitionKey),
                ConsistencyLevelEnum.BOUNDED
        );

        List<TextEmbeddingEntity> list = new ArrayList<>();
        for (RowRecord row : queryResultsWrapper.getRowRecords()) {
            String id = row.get(ID_FIELD_NAME).toString();
            List<Float> vector = (List<Float>) row.get(VECTOR_FIELD_NAME);
            String text = row.get(TEXT_FIELD_NAME).toString();
            String pKey = StringPool.EMPTY;
            if (StringUtil.isNotBlank(partitionKey)) {
                pKey = row.get(partitionKey).toString();
            }
            Boolean deleted = Func.toBoolean(row.get(DELETE_FIELD_NAME));
            Map<String, Object> metadata = JsonUtil.toMap(row.get(METADATA_FIELD_NAME).toString());
            TextSegment textSegment = null != metadata ? TextSegment.from(text, Metadata.from(metadata)) : TextSegment.from(text);
            TextEmbeddingEntity entity = TextEmbeddingEntity.from(id, Embedding.from(vector), textSegment, pKey, deleted);
            list.add(entity);
        }

        return list;
    }
}
