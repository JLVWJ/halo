package com.lvwj.halo.milvus.core;

import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.*;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.dml.UpsertParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.lvwj.halo.milvus.core.CollectionRequestBuilder.*;
import static com.lvwj.halo.milvus.core.MilvusEmbeddingStore.*;
import static io.milvus.grpc.DataType.*;
import static java.lang.String.format;

class CollectionOperationsExecutor {

    static void flush(MilvusServiceClient milvusClient, String collectionName) {
        FlushParam request = buildFlushRequest(collectionName);
        R<FlushResponse> response = milvusClient.flush(request);
        checkResponseNotFailed(response);
    }

    static boolean hasCollection(MilvusServiceClient milvusClient, String collectionName) {
        HasCollectionParam request = buildHasCollectionRequest(collectionName);
        R<Boolean> response = milvusClient.hasCollection(request);
        checkResponseNotFailed(response);
        return response.getData();
    }

    static void createCollection(MilvusServiceClient milvusClient, String collectionName, PartitionKey partitionKey, int dimension, boolean softDelete) {

        CollectionSchemaParam.Builder builder = CollectionSchemaParam.newBuilder()
                .addFieldType(FieldType.newBuilder()
                        .withName(ID_FIELD_NAME)
                        .withDataType(VarChar)
                        .withMaxLength(36)
                        .withPrimaryKey(true)
                        .withAutoID(false)
                        .withDescription("主键")
                        .build())
                .addFieldType(FieldType.newBuilder()
                        .withName(TEXT_FIELD_NAME)
                        .withDataType(VarChar)
                        .withMaxLength(65535)
                        .withDescription("向量文本")
                        .build())
                .addFieldType(FieldType.newBuilder()
                        .withName(METADATA_FIELD_NAME)
                        .withDataType(JSON)
                        .withDescription("动态元数据")
                        .build())
                .addFieldType(FieldType.newBuilder()
                        .withName(VECTOR_FIELD_NAME)
                        .withDataType(FloatVector)
                        .withDimension(dimension)
                        .withDescription("向量")
                        .build());
        if (softDelete) {
            builder.addFieldType(FieldType.newBuilder()
                    .withName(DELETE_FIELD_NAME)
                    .withDataType(Bool)
                    .withDescription("软删除")
                    .build());
        }
        if (null != partitionKey) {
            builder.addFieldType(FieldType.newBuilder()
                    .withName(partitionKey.getFieldName())
                    .withDataType(partitionKey.getDataType())
                    .withDescription("分区键:" + partitionKey.getFieldName())
                    .withPartitionKey(true)
                    .build());
        }

        CreateCollectionParam request = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withSchema(builder.build())
                .build();

        R<RpcStatus> response = milvusClient.createCollection(request);
        checkResponseNotFailed(response);
    }

    static void dropCollection(MilvusServiceClient milvusClient, String collectionName) {
        DropCollectionParam request = buildDropCollectionRequest(collectionName);
        R<RpcStatus> response = milvusClient.dropCollection(request);
        checkResponseNotFailed(response);
    }

    static void createIndex(MilvusServiceClient milvusClient,
                            String collectionName,
                            PartitionKey partitionKey,
                            IndexType indexType,
                            String indexParam,
                            MetricType metricType) {
        CreateIndexParam.Builder builder = CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName(VECTOR_FIELD_NAME)
                .withIndexType(indexType)
                .withIndexName("idx_" + VECTOR_FIELD_NAME)
                .withMetricType(metricType);
        if (!StringUtils.hasLength(indexParam)) {
            indexParam = defaultIndexParam(indexType);
        }
        if (StringUtils.hasLength(indexParam)) {
            builder.withExtraParam(indexParam);
        }
        CreateIndexParam request = builder.build();

        R<RpcStatus> response = milvusClient.createIndex(request);
        checkResponseNotFailed(response);

        if (null != partitionKey) {
            response = milvusClient.createIndex(CreateIndexParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withFieldName(partitionKey.getFieldName())
                    .withIndexType(partitionKey.getDataType().equals(VarChar) ? IndexType.INVERTED : IndexType.STL_SORT)
                    .withIndexName("idx_" + partitionKey.getFieldName())
                    .build());
            checkResponseNotFailed(response);
        }
    }

    private static String defaultIndexParam(IndexType indexType) {
        String indexParam = null;
        if (indexType.getName().contains("IVF")) {
            indexParam = "{\"nlist\":1024}";
        } else if (indexType.equals(IndexType.HNSW)) {
            indexParam = "{ \"M\": 8, \"efConstruction\": 64 }";
        }
        return indexParam;
    }

    static void insert(MilvusServiceClient milvusClient, String collectionName, List<InsertParam.Field> fields) {
        InsertParam request = buildInsertRequest(collectionName, fields);
        R<MutationResult> response = milvusClient.insert(request);
        checkResponseNotFailed(response);
    }

    static void upsert(MilvusServiceClient milvusClient, String collectionName, List<UpsertParam.Field> fields) {
        UpsertParam request = buildUpsertRequest(collectionName, fields);
        R<MutationResult> response = milvusClient.upsert(request);
        checkResponseNotFailed(response);
    }

    static void loadCollectionInMemory(MilvusServiceClient milvusClient, String collectionName) {
        LoadCollectionParam request = buildLoadCollectionInMemoryRequest(collectionName);
        R<RpcStatus> response = milvusClient.loadCollection(request);
        checkResponseNotFailed(response);
    }

    static SearchResultsWrapper search(MilvusServiceClient milvusClient, SearchParam searchRequest) {
        R<SearchResults> response = milvusClient.search(searchRequest);
        checkResponseNotFailed(response);

        return new SearchResultsWrapper(response.getData().getResults());
    }

    static QueryResultsWrapper queryForVectors(MilvusServiceClient milvusClient,
                                               String collectionName,
                                               List<String> rowIds,
                                               ConsistencyLevelEnum consistencyLevel) {
        QueryParam request = buildQueryRequest(collectionName, rowIds, consistencyLevel);
        R<QueryResults> response = milvusClient.query(request);
        checkResponseNotFailed(response);

        return new QueryResultsWrapper(response.getData());
    }

    static void removeForVector(MilvusServiceClient milvusClient,
                                String collectionName,
                                String expr) {
        R<MutationResult> response = milvusClient.delete(buildDeleteRequest(collectionName, expr));
        checkResponseNotFailed(response);
    }

    private static <T> void checkResponseNotFailed(R<T> response) {
        if (response == null) {
            throw new RequestToMilvusFailedException("Request to Milvus DB failed. Response is null");
        } else if (response.getStatus() != R.Status.Success.getCode()) {
            String message = format("Request to Milvus DB failed. Response status:'%d'.%n", response.getStatus());
            throw new RequestToMilvusFailedException(message, response.getException());
        }
    }
}
