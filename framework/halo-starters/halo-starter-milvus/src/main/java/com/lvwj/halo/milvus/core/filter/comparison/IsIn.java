package com.lvwj.halo.milvus.core.filter.comparison;

import com.lvwj.halo.milvus.core.filter.Filter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static dev.langchain4j.internal.ValidationUtils.*;
import static java.util.Collections.unmodifiableSet;

@ToString
@EqualsAndHashCode
public class IsIn implements Filter {

    private final String key;
    private final Collection<?> comparisonValues;

    public IsIn(String key, Collection<?> comparisonValues) {
        this.key = ensureNotBlank(key, "key");
        Set<?> copy = new HashSet<>(ensureNotEmpty(comparisonValues, "comparisonValues with key '" + key + "'"));
        comparisonValues.forEach(value -> ensureNotNull(value, "comparisonValue with key '" + key + "'"));
        this.comparisonValues = unmodifiableSet(copy);
    }

    public String key() {
        return key;
    }

    public Collection<?> comparisonValues() {
        return comparisonValues;
    }
}
