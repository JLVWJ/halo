package com.lvwj.halo.milvus.core.filter.comparison;

import com.lvwj.halo.milvus.core.filter.Filter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

@ToString
@EqualsAndHashCode
public class IsGreaterThanOrEqualTo implements Filter {

    private final String key;
    private final Comparable<?> comparisonValue;

    public IsGreaterThanOrEqualTo(String key, Comparable<?> comparisonValue) {
        this.key = ensureNotBlank(key, "key");
        this.comparisonValue = ensureNotNull(comparisonValue, "comparisonValue with key '" + key + "'");
    }

    public String key() {
        return key;
    }

    public Comparable<?> comparisonValue() {
        return comparisonValue;
    }
}
