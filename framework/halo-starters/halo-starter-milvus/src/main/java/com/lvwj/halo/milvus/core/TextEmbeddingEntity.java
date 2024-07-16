package com.lvwj.halo.milvus.core;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import lombok.Getter;

import java.io.Serializable;

/**
 * 文本向量化实体数据
 *
 * @author lvweijie
 * @date 2024年07月11日 17:43
 */
@Getter
public class TextEmbeddingEntity implements Serializable {

    public TextEmbeddingEntity() {

    }

    public TextEmbeddingEntity(String id, Embedding embedding, TextSegment textSegment) {
        this.id = id;
        this.embedding = embedding;
        this.textSegment = textSegment;
    }

    public TextEmbeddingEntity(String id, Embedding embedding, TextSegment textSegment, String partitionKey) {
        this(id, embedding, textSegment);
        this.partitionKey = partitionKey;
    }

    public static TextEmbeddingEntity from(String id, Embedding embedding, TextSegment textSegment) {
        return new TextEmbeddingEntity(id, embedding, textSegment);
    }

    public static TextEmbeddingEntity from(String id, Embedding embedding, TextSegment textSegment, String partitionKey) {
        return new TextEmbeddingEntity(id, embedding, textSegment, partitionKey);
    }

    private String id;

    private Embedding embedding;

    private TextSegment textSegment;

    private String partitionKey;

}
