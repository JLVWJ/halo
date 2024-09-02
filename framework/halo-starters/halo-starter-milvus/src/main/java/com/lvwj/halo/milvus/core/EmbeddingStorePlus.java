package com.lvwj.halo.milvus.core;

import com.lvwj.halo.common.utils.StringUtil;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.filter.Filter;

import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * 扩展增强EmbeddingStore
 *
 * @author lvweijie
 * @date 2024年07月15日 19:35
 */
public interface EmbeddingStorePlus {

    void dropCollection(String collectionName);
    default void add(TextEmbeddingEntity entity) {
        addList(singletonList(entity));
    }

    default void save(TextEmbeddingEntity entity) {
        saveList(singletonList(entity));
    }

    default void add(String id, Embedding embedding, TextSegment textSegment, String partitionKey) {
        add(TextEmbeddingEntity.from(id, embedding, textSegment, partitionKey));
    }

    default void save(String id, Embedding embedding, TextSegment textSegment, String partitionKey) {
        save(TextEmbeddingEntity.from(id, embedding, textSegment, partitionKey));
    }

    void addList(List<TextEmbeddingEntity> entities);

    void saveList(List<TextEmbeddingEntity> entities);

    /**
     * 根据条件查询并更新实体，如查不到则插入实体
     *
     * @author lvweijie
     * @date 2024/8/10 14:38
     * @param filter 查询条件
     * @param updateEntity 实体
     */
    void save(Filter filter, TextEmbeddingEntity updateEntity);

    /**
     * 根据条件查询并更新实体
     *
     * @author lvweijie
     * @date 2024/8/10 14:38
     * @param filter 查询条件
     * @param updateEntity 实体
     */
    void update(Filter filter, TextEmbeddingEntity updateEntity);

    default void remove(String id) {
        if (StringUtil.isNotBlank(id)) {
            this.removeAll(singletonList(id));
        }
    }

    void removeAll(Collection<String> ids);

    void removeAll(Filter filter);

    void removeAll();

    /**
     * 恢复数据
     *
     * @author lvweijie
     * @date 2024/7/30 17:15
     * @param id id
     */
    default void recover(String id) {
        if (StringUtil.isNotBlank(id)) {
            this.recoverAll(singletonList(id));
        }
    }

    /**
     * 恢复数据
     *
     * @author lvweijie
     * @date 2024/7/30 17:15
     * @param ids ids
     */
    void recoverAll(Collection<String> ids);

    /**
     * 恢复数据
     *
     * @author lvweijie
     * @date 2024/7/30 17:15
     * @param filter filter
     */
    void recoverAll(Filter filter);

    /**
     * 恢复数据
     *
     * @author lvweijie
     * @date 2024/7/30 17:15
     */
    void recoverAll();


    EmbeddingSearchResult<TextSegment> search(EmbeddingSearchExtRequest embeddingSearchExtRequest);

    default EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
        return search(EmbeddingSearchExtRequest.from(request));
    }
}
