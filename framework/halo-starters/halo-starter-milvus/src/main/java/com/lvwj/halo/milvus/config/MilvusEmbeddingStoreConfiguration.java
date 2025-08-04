package com.lvwj.halo.milvus.config;

import cn.hutool.extra.spring.SpringUtil;
import com.lvwj.halo.milvus.core.MilvusEmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author lvweijie
 * @date 2024年07月08日 17:10
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "halo.milvus", value = "enabled", havingValue = "true")
@EnableConfigurationProperties(MilvusEmbeddingStoreProperties.class)
public class MilvusEmbeddingStoreConfiguration {

    @Resource
    private MilvusEmbeddingStoreProperties properties;

    @Bean("buildMilvusEmbeddingStore")
    public SmartInitializingSingleton buildMilvusEmbeddingStore() {
        return () -> {
            if (null == properties || !properties.isEnabled() || null == properties.getCollections() || properties.getCollections().isEmpty())
                return;

            MilvusEmbeddingStore.Builder builder = MilvusEmbeddingStore.builder();
            if (StringUtils.hasLength(properties.getUri())) {
                builder.uri(properties.getUri().trim());
            }
            if (StringUtils.hasLength(properties.getHost())) {
                builder.host(properties.getHost().trim());
            }
            if (null != properties.getPort()) {
                builder.port(properties.getPort());
            }
            if (StringUtils.hasLength(properties.getUsername())) {
                builder.username(properties.getUsername().trim());
            }
            if (StringUtils.hasLength(properties.getPassword())) {
                builder.password(properties.getPassword().trim());
            }
            if (StringUtils.hasLength(properties.getToken())) {
                builder.token(properties.getToken().trim());
            }
            if (StringUtils.hasLength(properties.getDatabaseName())) {
                builder.databaseName(properties.getDatabaseName().trim());
            }

            for (Map.Entry<String, MilvusEmbeddingStoreProperties.CollectionEntry> entry : properties.getCollections().entrySet()) {
                if (!entry.getValue().isEnabled()) continue;

                builder.collectionName(entry.getKey().trim());
                builder.indexParam(entry.getValue().getIndexParam());
                builder.partitionKey(entry.getValue().getPartitionKey());
                builder.dimension(entry.getValue().getDimension());
                builder.metricType(entry.getValue().getMetricType());
                builder.indexType(entry.getValue().getIndexType());
                builder.consistencyLevel(entry.getValue().getConsistencyLevel());
                builder.retrieveEmbeddingsOnSearch(entry.getValue().getRetrieveEmbeddingsOnSearch());
                builder.autoFlushOnInsert(entry.getValue().getAutoFlushOnInsert());
                builder.softDelete(entry.getValue().getSoftDelete());
                SpringUtil.registerBean(entry.getKey(), builder.build());
            }
        };
    }
}
