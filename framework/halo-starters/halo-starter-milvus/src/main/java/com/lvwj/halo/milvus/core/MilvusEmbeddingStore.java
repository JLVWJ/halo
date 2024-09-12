package com.lvwj.halo.milvus.core;

import com.lvwj.halo.common.enums.BaseErrorEnum;
import com.lvwj.halo.common.utils.Assert;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import com.lvwj.halo.milvus.core.filter.Filter;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.lvwj.halo.common.utils.Func.getOrDefault;
import static com.lvwj.halo.milvus.core.CollectionFieldConstant.*;
import static com.lvwj.halo.milvus.core.CollectionOperationsExecutor.*;
import static com.lvwj.halo.milvus.core.CollectionRequestBuilder.*;
import static com.lvwj.halo.milvus.core.Mapper.*;
import static com.lvwj.halo.milvus.core.MilvusFilterMapper.*;
import static io.milvus.common.clientenum.ConsistencyLevelEnum.EVENTUALLY;
import static io.milvus.param.IndexType.FLAT;
import static io.milvus.param.MetricType.COSINE;
import static java.lang.String.format;

/**
 * Represents an <a href="https://milvus.io/">Milvus</a> index as an embedding store.
 * <br>
 * Supports both local and <a href="https://zilliz.com/">managed</a> Milvus instances.
 * <br>
 * Supports storing {@link Metadata} and filtering by it using a {@link Filter}
 * (provided inside an {@link EmbeddingSearchRequest}).
 */
public class MilvusEmbeddingStore implements EmbeddingStorePlus {

    private final MilvusServiceClient milvusClient;
    private final String collectionName;
    private final PartitionKey partitionKey;
    private final MetricType metricType;
    private final ConsistencyLevelEnum consistencyLevel;
    private final boolean retrieveEmbeddingsOnSearch;
    private final boolean autoFlushOnInsert;
    private final Boolean softDelete;

    private String getPartitionKeyFieldName() {
        return null != this.partitionKey ? this.partitionKey.getFieldName() : null;
    }

    public MilvusEmbeddingStore(
            String host,
            Integer port,
            String collectionName,
            Integer dimension,
            IndexType indexType,
            String indexParam,
            MetricType metricType,
            PartitionKey partitionKey,
            String uri,
            String token,
            String username,
            String password,
            ConsistencyLevelEnum consistencyLevel,
            Boolean retrieveEmbeddingsOnSearch,
            Boolean autoFlushOnInsert,
            String databaseName,
            Boolean softDelete
    ) {
        ConnectParam.Builder connectBuilder = ConnectParam
                .newBuilder()
                .withHost(getOrDefault(host, "localhost"))
                .withPort(getOrDefault(port, 19530))
                .withUri(uri)
                .withToken(token)
                .withAuthorization(getOrDefault(username, ""), getOrDefault(password, ""));

        if (databaseName != null) {
            connectBuilder.withDatabaseName(databaseName);
        }

        this.milvusClient = new MilvusServiceClient(connectBuilder.build());
        this.collectionName = getOrDefault(collectionName, "default");
        this.metricType = getOrDefault(metricType, COSINE);
        this.consistencyLevel = getOrDefault(consistencyLevel, EVENTUALLY);
        this.retrieveEmbeddingsOnSearch = getOrDefault(retrieveEmbeddingsOnSearch, false);
        this.autoFlushOnInsert = getOrDefault(autoFlushOnInsert, false);
        this.softDelete = getOrDefault(softDelete, false);
        this.partitionKey = partitionKey;

        if (!hasCollection(this.milvusClient, this.collectionName)) {
            createCollection(this.milvusClient, this.collectionName, this.partitionKey, Objects.requireNonNull(dimension), this.softDelete);
            createIndex(this.milvusClient, this.collectionName, this.partitionKey, getOrDefault(indexType, FLAT), indexParam, this.metricType);
        }

        loadCollectionInMemory(this.milvusClient, collectionName);
    }

    @Override
    public void dropCollection(String collectionName) {
        CollectionOperationsExecutor.dropCollection(this.milvusClient, collectionName);
    }

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest embeddingSearchRequest) {
        SearchParam searchParam = buildSearchRequest(
                collectionName,
                embeddingSearchRequest.getQueryEmbedding().vectorAsList(),
                embeddingSearchRequest.getFilter(),
                embeddingSearchRequest.getMaxResults(),
                metricType,
                consistencyLevel,
                embeddingSearchRequest.getParams(),
                embeddingSearchRequest.getGroupByFieldName(),
                embeddingSearchRequest.getPartitionNames(),
                partitionKey,
                softDelete);
        SearchResultsWrapper resultsWrapper = CollectionOperationsExecutor.search(milvusClient, searchParam);
        var matches = toEmbeddingMatches(milvusClient, resultsWrapper, collectionName, consistencyLevel, retrieveEmbeddingsOnSearch);
        var result = matches.stream().filter(match -> match.score() >= embeddingSearchRequest.getMinScore()).toList();
        return new EmbeddingSearchResult<>(result);
    }

    @Override
    public void addList(List<TextEmbeddingEntity> entities) {
        upsertAllInternal(entities, true);
    }

    /**
     * save = upsert = add or update
     */
    @Override
    public void saveList(List<TextEmbeddingEntity> entities) {
        upsertAllInternal(entities, false);
    }

    @Override
    public void save(Filter filter, TextEmbeddingEntity updateEntity){
        Assert.notNullOrEmpty(filter, BaseErrorEnum.PARAM_EMPTY_ERROR, "MilvusEmbeddingStore.save[filter]");
        upsertByExpr(map(filter,getPartitionKeyFieldName()),updateEntity);
    }

    @Override
    public void update(Filter filter, TextEmbeddingEntity updateEntity) {
        Assert.notNullOrEmpty(filter, BaseErrorEnum.PARAM_EMPTY_ERROR, "MilvusEmbeddingStore.update[filter]");
        updateByExpr(map(filter,getPartitionKeyFieldName()),updateEntity);
    }

    /**
     * 根据条件查询并更新实体，如查不到则插入实体
     *
     * @author lvweijie
     * @date 2024/8/10 14:38
     * @param expr 查询条件
     * @param updateEntity 实体
     */
    private void upsertByExpr(String expr, TextEmbeddingEntity updateEntity) {
        Assert.notNullOrEmpty(expr, BaseErrorEnum.PARAM_EMPTY_ERROR, "MilvusEmbeddingStore.upsertByExpr[expr]");
        Assert.notNullOrEmpty(updateEntity, BaseErrorEnum.PARAM_EMPTY_ERROR, "MilvusEmbeddingStore.upsertByExpr[updateEntity]");

        List<String> ids = new ArrayList<>();
        List<TextSegment> textSegments = new ArrayList<>();
        List<Embedding> embeddings = new ArrayList<>();
        List<String> partitionKeys = new ArrayList<>();
        List<Boolean> deletes = new ArrayList<>();

        List<TextEmbeddingEntity> entities = queryEntities(this.milvusClient, this.collectionName, getPartitionKeyFieldName(), expr);
        if (CollectionUtils.isEmpty(entities)) { //查不到，则用updateEntity插入
            Assert.notNullOrEmpty(updateEntity.getId(), BaseErrorEnum.PARAM_EMPTY_ERROR, "MilvusEmbeddingStore.upsertByExpr[updateEntity.id]");
            Assert.notNullOrEmpty(updateEntity.getTextSegment(), BaseErrorEnum.PARAM_EMPTY_ERROR, "MilvusEmbeddingStore.upsertByExpr[updateEntity.textSegment]");
            Assert.notNullOrEmpty(updateEntity.getEmbedding(), BaseErrorEnum.PARAM_EMPTY_ERROR, "MilvusEmbeddingStore.upsertByExpr[updateEntity.embedding]");
            ids.add(updateEntity.getId());
            textSegments.add(updateEntity.getTextSegment());
            embeddings.add(updateEntity.getEmbedding());
            if (StringUtils.hasLength(updateEntity.getPartitionKey())) {
                partitionKeys.add(updateEntity.getPartitionKey());
            }
            deletes.add(Optional.ofNullable(updateEntity.getDeleted()).orElse(Boolean.FALSE));
        } else { //查的到，则用updateEntity更新查到的数据
            for (TextEmbeddingEntity entity : entities) {
                if (!entity.update(updateEntity)) continue;
                ids.add(entity.getId());
                textSegments.add(entity.getTextSegment());
                embeddings.add(entity.getEmbedding());
                if (StringUtils.hasLength(entity.getPartitionKey())) {
                    partitionKeys.add(entity.getPartitionKey());
                }
                deletes.add(entity.getDeleted());
            }
        }
        if (!ids.isEmpty())
            upsertAllInternal(ids, textSegments, embeddings, partitionKeys, deletes, false);
    }

    /**
     * 根据条件查询并更新实体
     *
     * @author lvweijie
     * @date 2024/8/10 14:38
     * @param expr 查询条件
     * @param updateEntity 实体
     */
    private void updateByExpr(String expr, TextEmbeddingEntity updateEntity) {
        Assert.notNullOrEmpty(expr, BaseErrorEnum.PARAM_EMPTY_ERROR,"MilvusEmbeddingStore.updateByExpr[expr]");
        Assert.notNullOrEmpty(updateEntity, BaseErrorEnum.PARAM_EMPTY_ERROR,"MilvusEmbeddingStore.updateByExpr[updateEntity]");

        List<String> ids = new ArrayList<>();
        List<TextSegment> textSegments = new ArrayList<>();
        List<Embedding> embeddings = new ArrayList<>();
        List<String> partitionKeys = new ArrayList<>();
        List<Boolean> deletes = new ArrayList<>();
        List<TextEmbeddingEntity> entities = queryEntities(this.milvusClient, this.collectionName, getPartitionKeyFieldName(), expr);
        for (TextEmbeddingEntity entity : entities) {
            if (!entity.update(updateEntity)) continue;
            ids.add(entity.getId());
            textSegments.add(entity.getTextSegment());
            embeddings.add(entity.getEmbedding());
            if (StringUtils.hasLength(entity.getPartitionKey())) {
                partitionKeys.add(entity.getPartitionKey());
            }
            deletes.add(entity.getDeleted());
        }
        if (!ids.isEmpty())
            upsertAllInternal(ids, textSegments, embeddings, partitionKeys, deletes, false);
    }

    /**
     *
     * @author lvweijie
     * @date 2024/7/30 20:09
     * @param entities entities
     * @param insertOrUpsert  true: insert, false: upsert
     */
    private void upsertAllInternal(List<TextEmbeddingEntity> entities, boolean insertOrUpsert) {
        if (CollectionUtils.isEmpty(entities)) return;
        List<String> ids = new ArrayList<>(entities.size());
        List<TextSegment> textSegments = new ArrayList<>(entities.size());
        List<Embedding> embeddings = new ArrayList<>(entities.size());
        List<String> partitionKeys = new ArrayList<>(entities.size());
        List<Boolean> deletes = new ArrayList<>(entities.size());
        Map<String, TextEmbeddingEntity> entityMap = null;
        if (!insertOrUpsert) { // 用主键去查已存在的数据
            List<String> rowIds = entities.stream().map(TextEmbeddingEntity::getId).toList();
            entityMap = queryEntities(this.milvusClient, this.collectionName, getPartitionKeyFieldName(), rowIds);
        }
        for (TextEmbeddingEntity entry : entities) {
            TextEmbeddingEntity entity = Optional.ofNullable(entityMap).map(s -> s.get(entry.getId())).orElse(null);
            if (null != entity) {
                if (!entity.update(entry)) continue;
            } else {
                entity = entry;
            }
            ids.add(entity.getId());
            textSegments.add(entity.getTextSegment());
            embeddings.add(entity.getEmbedding());
            if (StringUtils.hasLength(entity.getPartitionKey())) {
                partitionKeys.add(entity.getPartitionKey());
            }
            deletes.add(entity.getDeleted());
        }
        if (!ids.isEmpty())
            upsertAllInternal(ids, textSegments, embeddings, partitionKeys, deletes, insertOrUpsert);
    }

    private void upsertAllInternal(List<String> ids, List<TextSegment> textSegments, List<Embedding> embeddings, List<String> partitionKeys, List<Boolean> deletes, boolean insertOrUpsert) {
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(ID_FIELD_NAME, ids));
        fields.add(new InsertParam.Field(TEXT_FIELD_NAME, toScalars(textSegments, ids.size())));
        fields.add(new InsertParam.Field(METADATA_FIELD_NAME, toMetadataJsons(textSegments, ids.size())));
        fields.add(new InsertParam.Field(VECTOR_FIELD_NAME, toVectors(embeddings)));
        if (softDelete) {
            fields.add(new InsertParam.Field(DELETE_FIELD_NAME, deletes));
        }
        if (null != partitionKey) {
            if (CollectionUtils.isEmpty(partitionKeys) || partitionKeys.size() != ids.size()) {
                throw new IllegalArgumentException("partitionKey shouldn't be null or empty!");
            }
            if (partitionKey.getDataType().equals(DataType.VarChar)) {
                fields.add(new InsertParam.Field(partitionKey.getFieldName(), partitionKeys));
            } else if (partitionKey.getDataType().equals(DataType.Int64)) {
                fields.add(new InsertParam.Field(partitionKey.getFieldName(), partitionKeys.stream().map(Long::valueOf).toList()));
            } else if (partitionKey.getDataType().name().startsWith("Int")) {
                fields.add(new InsertParam.Field(partitionKey.getFieldName(), partitionKeys.stream().map(Integer::valueOf).toList()));
            }
        }
        if (insertOrUpsert) {
            insert(this.milvusClient, this.collectionName, fields);
        } else {
            upsert(this.milvusClient, this.collectionName, fields);
        }
        if (autoFlushOnInsert) {
            flush(this.milvusClient, this.collectionName);
        }
    }

    @Override
    public void removeAll(Collection<String> ids) {
        Assert.notNullOrEmpty(ids, BaseErrorEnum.PARAM_EMPTY_ERROR, "ids");
        doRemoveAll(format("%s in %s", ID_FIELD_NAME, formatValues(ids)));
    }


    @Override
    public void removeAll(Filter filter) {
        Assert.notNullOrEmpty(filter, BaseErrorEnum.PARAM_EMPTY_ERROR, "filter");
        doRemoveAll(map(filter, getPartitionKeyFieldName()));
    }

    @Override
    public void removeAll() {
        doRemoveAll(format("%s != \"\"", ID_FIELD_NAME));
    }

    @Override
    public void recoverAll(Collection<String> ids) {
        Assert.notNullOrEmpty(ids, BaseErrorEnum.PARAM_EMPTY_ERROR, "ids");
        doRecoverAll(format("%s in %s", ID_FIELD_NAME, formatValues(ids)));
    }

    @Override
    public void recoverAll(Filter filter) {
        Assert.notNullOrEmpty(filter, BaseErrorEnum.PARAM_EMPTY_ERROR, "filter");
        doRecoverAll(map(filter, getPartitionKeyFieldName()));
    }

    @Override
    public void recoverAll() {
        doRecoverAll(format("%s != \"\"", ID_FIELD_NAME));
    }

    private void doRecoverAll(String expr) {
        if (softDelete) {
            updateByExpr(expr, TextEmbeddingEntity.from().deleted(Boolean.FALSE));
        }
    }

    private void doRemoveAll(String expr) {
        if (softDelete) {
            updateByExpr(expr, TextEmbeddingEntity.from().deleted(Boolean.TRUE));
        } else {
            delete(this.milvusClient, this.collectionName, expr);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String host;
        private Integer port;
        private String databaseName;
        private String collectionName;
        private Integer dimension;
        private IndexType indexType;
        private String indexParam = "{\"nlist\":2048}";
        private MetricType metricType;
        private PartitionKey partitionKey;
        private String uri;
        private String token;
        private String username;
        private String password;
        private ConsistencyLevelEnum consistencyLevel;
        private Boolean retrieveEmbeddingsOnSearch;
        private Boolean autoFlushOnInsert;

        private Boolean softDelete;

        /**
         * @param host The host of the self-managed Milvus instance.
         *             Default value: "localhost".
         * @return builder
         */
        public Builder host(String host) {
            this.host = host;
            return this;
        }

        /**
         * @param port The port of the self-managed Milvus instance.
         *             Default value: 19530.
         * @return builder
         */
        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        /**
         * @param databaseName Milvus name of database.
         *                     Default value: null. In this case default Milvus database name will be used.
         * @return builder
         */
        public Builder databaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        /**
         * @param collectionName The name of the Milvus collection.
         *                       If there is no such collection yet, it will be created automatically.
         *                       Default value: "default".
         * @return builder
         */
        public Builder collectionName(String collectionName) {
            this.collectionName = collectionName;
            return this;
        }

        /**
         * @param dimension The dimension of the embedding vector. (e.g. 384)
         *                  Mandatory if a new collection should be created.
         * @return builder
         */
        public Builder dimension(Integer dimension) {
            this.dimension = dimension;
            return this;
        }

        /**
         * @param indexType The type of the index.
         *                  Default value: FLAT.
         * @return builder
         */
        public Builder indexType(IndexType indexType) {
            this.indexType = indexType;
            return this;
        }

        /**
         * @param indexParam The param of the index.
         *                   Default value: {\"nlist\":2048}.
         * @return builder
         */
        public Builder indexParam(String indexParam) {
            this.indexParam = indexParam;
            return this;
        }

        /**
         * @param metricType The type of the metric used for similarity search.
         *                   Default value: COSINE.
         * @return builder
         */
        public Builder metricType(MetricType metricType) {
            this.metricType = metricType;
            return this;
        }

        public Builder partitionKey(PartitionKey partitionKey) {
            this.partitionKey = partitionKey;
            return this;
        }

        /**
         * @param uri The URI of the managed Milvus instance. (e.g. "https://xxx.api.gcp-us-west1.zillizcloud.com")
         * @return builder
         */
        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        /**
         * @param token The token (API key) of the managed Milvus instance.
         * @return builder
         */
        public Builder token(String token) {
            this.token = token;
            return this;
        }

        /**
         * @param username The username. See details <a href="https://milvus.io/docs/authenticate.md">here</a>.
         * @return builder
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * @param password The password. See details <a href="https://milvus.io/docs/authenticate.md">here</a>.
         * @return builder
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * @param consistencyLevel The consistency level used by Milvus.
         *                         Default value: EVENTUALLY.
         * @return builder
         */
        public Builder consistencyLevel(ConsistencyLevelEnum consistencyLevel) {
            this.consistencyLevel = consistencyLevel;
            return this;
        }

        /**
         * @param retrieveEmbeddingsOnSearch During a similarity search in Milvus (when calling findRelevant()),
         *                                   the embedding itself is not retrieved.
         *                                   To retrieve the embedding, an additional query is required.
         *                                   Setting this parameter to "true" will ensure that embedding is retrieved.
         *                                   Be aware that this will impact the performance of the search.
         *                                   Default value: false.
         * @return builder
         */
        public Builder retrieveEmbeddingsOnSearch(Boolean retrieveEmbeddingsOnSearch) {
            this.retrieveEmbeddingsOnSearch = retrieveEmbeddingsOnSearch;
            return this;
        }

        /**
         * @param autoFlushOnInsert Whether to automatically flush after each insert
         *                          ({@code add(...)} or {@code addAll(...)} methods).
         *                          Default value: false.
         *                          More info can be found
         *                          <a href="https://milvus.io/api-reference/pymilvus/v2.4.x/ORM/Collection/flush.md">here</a>.
         * @return builder
         */
        public Builder autoFlushOnInsert(Boolean autoFlushOnInsert) {
            this.autoFlushOnInsert = autoFlushOnInsert;
            return this;
        }

        /**
         * @param softDelete 是否软删除
         * @return builder
         */
        public Builder softDelete(Boolean softDelete) {
            this.softDelete = softDelete;
            return this;
        }

        public MilvusEmbeddingStore build() {
            return new MilvusEmbeddingStore(
                    host,
                    port,
                    collectionName,
                    dimension,
                    indexType,
                    indexParam,
                    metricType,
                    partitionKey,
                    uri,
                    token,
                    username,
                    password,
                    consistencyLevel,
                    retrieveEmbeddingsOnSearch,
                    autoFlushOnInsert,
                    databaseName,
                    softDelete
            );
        }
    }
}
