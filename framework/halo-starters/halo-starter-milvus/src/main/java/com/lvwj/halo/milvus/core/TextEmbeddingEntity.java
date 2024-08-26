package com.lvwj.halo.milvus.core;

import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.StringUtil;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public TextEmbeddingEntity(String id, Embedding embedding, TextSegment textSegment, String partitionKey, Boolean deleted) {
        this.id = id;
        this.embedding = embedding;
        this.textSegment = textSegment;
        this.partitionKey = partitionKey;
        if (null != deleted) {
            this.deleted = deleted;
        }
    }

    public static TextEmbeddingEntity from() {
        return TextEmbeddingEntity.from(null);
    }

    public static TextEmbeddingEntity from(String id) {
        return TextEmbeddingEntity.from(id, null, null);
    }

    public static TextEmbeddingEntity from(String id, Embedding embedding, TextSegment textSegment) {
        return TextEmbeddingEntity.from(id, embedding, textSegment, null);
    }

    public static TextEmbeddingEntity from(String id, Embedding embedding, TextSegment textSegment, String partitionKey) {
        return TextEmbeddingEntity.from(id, embedding, textSegment, partitionKey, Boolean.FALSE);
    }

    public static TextEmbeddingEntity from(String id, Embedding embedding, TextSegment textSegment, String partitionKey, Boolean deleted) {
        return new TextEmbeddingEntity(id, embedding, textSegment, partitionKey, deleted);
    }

    private String id;

    private Embedding embedding;

    private TextSegment textSegment;

    private String partitionKey;

    private Boolean deleted = Boolean.FALSE;

    public TextEmbeddingEntity deleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public boolean update(TextEmbeddingEntity entity) {
        if (null == entity) return false;
        boolean change = false;
        if (null != entity.getTextSegment() && !entity.getTextSegment().equals(this.textSegment)) {
            Map<String, Object> oldMap = Optional.ofNullable(this.textSegment.metadata()).map(Metadata::toMap).orElse(new HashMap<>());
            Metadata newMetadata = entity.getTextSegment().metadata();
            if (null != newMetadata) {
                oldMap.putAll(newMetadata.toMap());
            }
            String text = Func.isBlank(entity.getTextSegment().text()) ? this.textSegment.text() : entity.getTextSegment().text();
            this.textSegment = TextSegment.from(text, Metadata.from(oldMap));
            change = true;
        }
        if (null != entity.getEmbedding()) {
            this.embedding = entity.getEmbedding();
        }
        if (StringUtil.isNotBlank(entity.getPartitionKey()) && !entity.getPartitionKey().equals(this.partitionKey)) {
            this.partitionKey = entity.getPartitionKey();
            change = true;
        }
        if (null != entity.getDeleted() && entity.getDeleted() != this.deleted) {
            this.deleted = entity.getDeleted();
            change = true;
        }
        return change;
    }
}
