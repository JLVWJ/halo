package com.lvwj.halo.milvus.core.filter;

import dev.langchain4j.store.embedding.EmbeddingStore;
import com.lvwj.halo.milvus.core.filter.comparison.*;
import com.lvwj.halo.milvus.core.filter.logical.And;
import com.lvwj.halo.milvus.core.filter.logical.Not;
import com.lvwj.halo.milvus.core.filter.logical.Or;

/**
 * This class represents a filter that can be applied during search in an {@link EmbeddingStore}.
 * <br>
 * Many {@link EmbeddingStore}s support a feature called metadata filtering. A {@code Filter} can be used for this.
 * <br>
 * A {@code Filter} object can represent simple (e.g. {@code type = 'documentation'})
 * and composite (e.g. {@code type = 'documentation' AND year > 2020}) filter expressions in
 * an {@link EmbeddingStore}-agnostic way.
 * <br>
 * Each {@link EmbeddingStore} implementation that supports metadata filtering is mapping {@link Filter}
 * into it's native filter expression.
 *
 * @see IsEqualTo
 * @see IsNotEqualTo
 * @see IsGreaterThan
 * @see IsGreaterThanOrEqualTo
 * @see IsLessThan
 * @see IsLessThanOrEqualTo
 * @see IsIn
 * @see IsNotIn
 * @see And
 * @see Not
 * @see Or
 */
public interface Filter {

    default Filter and(Filter filter) {
        return and(this, filter);
    }

    static Filter and(Filter left, Filter right) {
        return new And(left, right);
    }

    default Filter or(Filter filter) {
        return or(this, filter);
    }

    static Filter or(Filter left, Filter right) {
        return new Or(left, right);
    }

    static Filter not(Filter expression) {
        return new Not(expression);
    }
}