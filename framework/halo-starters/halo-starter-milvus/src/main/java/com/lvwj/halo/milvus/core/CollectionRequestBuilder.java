package com.lvwj.halo.milvus.core;

import com.lvwj.halo.common.utils.StringPool;
import com.lvwj.halo.milvus.core.filter.Filter;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.MetricType;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.FlushParam;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.lvwj.halo.milvus.core.CollectionFieldConstant.*;
import static com.lvwj.halo.milvus.core.MilvusFilterMapper.formatValues;
import static com.lvwj.halo.milvus.core.MilvusFilterMapper.map;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

class CollectionRequestBuilder {

    static FlushParam buildFlushRequest(String collectionName) {
        return FlushParam.newBuilder()
                .withCollectionNames(singletonList(collectionName))
                .build();
    }

    static HasCollectionParam buildHasCollectionRequest(String collectionName) {
        return HasCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
    }

    static DropCollectionParam buildDropCollectionRequest(String collectionName) {
        return DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
    }

    static InsertParam buildInsertRequest(String collectionName, List<InsertParam.Field> fields) {
        return InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();
    }

    static UpsertParam buildUpsertRequest(String collectionName, List<UpsertParam.Field> fields) {
        return UpsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();
    }

    static LoadCollectionParam buildLoadCollectionInMemoryRequest(String collectionName) {
        return LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
    }

    static SearchParam buildSearchRequest(String collectionName,
                                          List<Float> vector,
                                          Filter filter,
                                          int maxResults,
                                          MetricType metricType,
                                          ConsistencyLevelEnum consistencyLevel,
                                          String params,
                                          String groupByFieldName,
                                          List<String> partitionNames,
                                          PartitionKey partitionKey,
                                          boolean softDelete) {
        SearchParam.Builder builder = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withFloatVectors(singletonList(vector))
                .withVectorFieldName(VECTOR_FIELD_NAME)
                .withTopK(maxResults)
                .withMetricType(metricType)
                .withConsistencyLevel(consistencyLevel)
                .withOutFields(asList(ID_FIELD_NAME, TEXT_FIELD_NAME, METADATA_FIELD_NAME));

        String filterStr = StringPool.EMPTY;
        if (filter != null) {
            String pKey = Optional.ofNullable(partitionKey).map(PartitionKey::getFieldName).orElse(null);
            filterStr = MilvusFilterMapper.map(filter, pKey);
        }
        if (softDelete) {
            String deleteStr = DELETE_FIELD_NAME + "==false";
            filterStr = StringPool.EMPTY.equals(filterStr) ? deleteStr : filterStr + " and " + deleteStr;
        }
        if (StringUtils.hasLength(filterStr)) {
            builder.withExpr(filterStr);
        }

        if (StringUtils.hasLength(params)) {
            builder.withParams(params);
        }

        if (StringUtils.hasLength(groupByFieldName)) {
            builder.withGroupByFieldName(groupByFieldName);
        }

        if (!CollectionUtils.isEmpty(partitionNames)) {
            builder.withPartitionNames(partitionNames);
        }

        return builder.build();
    }

    static QueryParam buildQueryRequest(String collectionName,
                                        List<String> rowIds,
                                        List<String> outFields,
                                        ConsistencyLevelEnum consistencyLevel) {
        return buildQueryRequest(collectionName, format("%s in %s", ID_FIELD_NAME, formatValues(rowIds)), outFields, consistencyLevel);
    }

    static QueryParam buildQueryRequest(String collectionName,
                                        String expr,
                                        List<String> outFields,
                                        ConsistencyLevelEnum consistencyLevel) {
        QueryParam.Builder builder = QueryParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(expr)
                .withOutFields(outFields)
                .withConsistencyLevel(consistencyLevel);
        if (!CollectionUtils.isEmpty(outFields)) {
            builder.withOutFields(outFields);
        }
        return builder.build();
    }

    static QueryParam buildQueryRequest(String collectionName,
                                        String partitionKey,
                                        Filter filter,
                                        List<String> outFields,
                                        ConsistencyLevelEnum consistencyLevel) {
        QueryParam.Builder builder = QueryParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(map(filter, partitionKey))
                .withOutFields(outFields)
                .withConsistencyLevel(consistencyLevel);
        if (!CollectionUtils.isEmpty(outFields)) {
            builder.withOutFields(outFields);
        }
        return builder.build();
    }

    static DeleteParam buildDeleteRequest(String collectionName, String expr) {
        return DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(expr)
                .build();
    }
}
