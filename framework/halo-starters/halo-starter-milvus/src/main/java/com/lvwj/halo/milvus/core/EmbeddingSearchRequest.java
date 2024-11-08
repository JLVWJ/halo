package com.lvwj.halo.milvus.core;

import com.lvwj.halo.milvus.core.filter.Filter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

import static com.lvwj.halo.common.utils.Func.getOrDefault;
import static dev.langchain4j.internal.ValidationUtils.*;


@Getter
@ToString
@EqualsAndHashCode
public class EmbeddingSearchRequest {

    private final Embedding queryEmbedding;
    private final int maxResults;
    private final double minScore;
    private final Filter filter;

    private final String params;
    private final String groupByFieldName;
    private final List<String> partitionNames;

    /**
     * Creates an instance of an EmbeddingSearchRequest.
     *
     * @param queryEmbedding The embedding used as a reference. Found embeddings should be similar to this one.
     *                       This is a mandatory parameter.
     * @param maxResults     The maximum number of embeddings to return. This is an optional parameter. Default: 3
     * @param minScore       The minimum score, ranging from 0 to 1 (inclusive).
     *                       Only embeddings with a score &gt;= minScore will be returned.
     *                       This is an optional parameter. Default: 0
     * @param filter         The filter to be applied to the {@link Metadata} during search.
     *                       Only {@link TextSegment}s whose {@link Metadata}
     *                       matches the {@link Filter} will be returned.
     *                       Please note that not all {@link EmbeddingStore}s support this feature yet.
     *                       This is an optional parameter. Default: no filtering
     */
    @Builder
    public EmbeddingSearchRequest(Embedding queryEmbedding, Integer maxResults, Double minScore, Filter filter, String params, String groupByFieldName, List<String> partitionNames) {
        this.queryEmbedding = ensureNotNull(queryEmbedding, "queryEmbedding");
        this.maxResults = ensureGreaterThanZero(getOrDefault(maxResults, 3), "maxResults");
        this.minScore = ensureBetween(getOrDefault(minScore, 0.0), 0.0, 1.0, "minScore");
        this.filter = filter;
        this.params = params;
        this.groupByFieldName = groupByFieldName;
        this.partitionNames = partitionNames;
    }

    public Embedding queryEmbedding() {
        return queryEmbedding;
    }

    public int maxResults() {
        return maxResults;
    }

    public double minScore() {
        return minScore;
    }

    public Filter filter() {
        return filter;
    }
}
