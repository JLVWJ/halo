package com.lvwj.halo.milvus.core;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.filter.Filter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * 扩展请求参数
 *
 * @author lvweijie
 * @date 2024年07月15日 19:43
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EmbeddingSearchExtRequest extends EmbeddingSearchRequest {

    private String params;

    private String groupByFieldName;

    private List<String> partitionNames;

    public EmbeddingSearchExtRequest(Embedding queryEmbedding, Integer maxResults, Double minScore, Filter filter, String params, String groupByFieldName, List<String> partitionNames) {
        super(queryEmbedding, maxResults, minScore, filter);
        this.params = params;
        this.groupByFieldName = groupByFieldName;
        this.partitionNames = partitionNames;
    }

    public EmbeddingSearchExtRequest(Embedding queryEmbedding, Integer maxResults, Double minScore, Filter filter) {
        super(queryEmbedding, maxResults, minScore, filter);
    }

    public EmbeddingSearchExtRequest(EmbeddingSearchRequest request) {
        super(request.queryEmbedding(), request.maxResults(), request.minScore(), request.filter());
    }

    public static EmbeddingSearchExtRequest from(EmbeddingSearchRequest request) {
        return new EmbeddingSearchExtRequest(request);

    }
}
