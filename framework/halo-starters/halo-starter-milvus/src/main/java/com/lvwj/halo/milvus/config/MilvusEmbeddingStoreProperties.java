package com.lvwj.halo.milvus.config;

import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Map;

/**
 *
 *
 * @author lvwj
 * @date 2023-02-10 18:37
 */
@Data
@ConfigurationProperties(prefix = MilvusEmbeddingStoreProperties.PREFIX)
public class MilvusEmbeddingStoreProperties implements Serializable {

    public static final String PREFIX = "halo.milvus";

    /**
     * 是否启用
     */
    private boolean enabled = true;

    private String host;
    private Integer port;
    private String uri;
    private String token;
    private String username;
    private String password;

    private String databaseName;

    private Map<String, CollectionEntry> collections;


    @Data
    @NoArgsConstructor
    public static class CollectionEntry implements Serializable {

        /**
         * 是否启用
         */
        private boolean enabled = true;
        private Integer dimension;
        private IndexType indexType = IndexType.HNSW;
        private MetricType metricType = MetricType.COSINE;
        private ConsistencyLevelEnum consistencyLevel = ConsistencyLevelEnum.BOUNDED;
        private Boolean autoFlushOnInsert = Boolean.TRUE;
        private Boolean retrieveEmbeddingsOnSearch = Boolean.TRUE;
    }
}
