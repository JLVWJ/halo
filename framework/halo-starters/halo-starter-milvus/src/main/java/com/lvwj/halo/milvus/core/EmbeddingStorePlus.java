package com.lvwj.halo.milvus.core;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.util.List;

/**
 * 扩展增强EmbeddingStore
 *
 * @author lvweijie
 * @date 2024年07月15日 19:35
 */
public interface EmbeddingStorePlus extends EmbeddingStore<TextSegment> {

    void add(TextEmbeddingEntity entity);

    void save(TextEmbeddingEntity entity);

    void add(String id, Embedding embedding, TextSegment textSegment, String partitionKey);

    void save(String id, Embedding embedding, TextSegment textSegment, String partitionKey);

    void addList(List<TextEmbeddingEntity> entities);
    void saveList(List<TextEmbeddingEntity> entities);

    EmbeddingSearchResult<TextSegment> search(EmbeddingSearchExtRequest embeddingSearchExtRequest);
}
