package com.lvwj.halo.milvus.core;

import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.*;
import dev.langchain4j.store.embedding.filter.logical.And;
import dev.langchain4j.store.embedding.filter.logical.Not;
import dev.langchain4j.store.embedding.filter.logical.Or;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

class MilvusMetadataFilterMapper {

    static String map(Filter filter, String partitionKey) {
        if (filter instanceof IsEqualTo) {
            return mapEqual((IsEqualTo) filter, partitionKey);
        } else if (filter instanceof IsNotEqualTo) {
            return mapNotEqual((IsNotEqualTo) filter, partitionKey);
        } else if (filter instanceof IsGreaterThan) {
            return mapGreaterThan((IsGreaterThan) filter, partitionKey);
        } else if (filter instanceof IsGreaterThanOrEqualTo) {
            return mapGreaterThanOrEqual((IsGreaterThanOrEqualTo) filter, partitionKey);
        } else if (filter instanceof IsLessThan) {
            return mapLessThan((IsLessThan) filter, partitionKey);
        } else if (filter instanceof IsLessThanOrEqualTo) {
            return mapLessThanOrEqual((IsLessThanOrEqualTo) filter, partitionKey);
        } else if (filter instanceof IsIn) {
            return mapIn((IsIn) filter, partitionKey);
        } else if (filter instanceof IsNotIn) {
            return mapNotIn((IsNotIn) filter, partitionKey);
        } else if (filter instanceof And) {
            return mapAnd((And) filter, partitionKey);
        } else if (filter instanceof Not) {
            return mapNot((Not) filter, partitionKey);
        } else if (filter instanceof Or) {
            return mapOr((Or) filter, partitionKey);
        } else {
            throw new UnsupportedOperationException("Unsupported filter type: " + filter.getClass().getName());
        }
    }

    private static String mapEqual(IsEqualTo isEqualTo, String partitionKey) {
        return format("%s == %s", formatKey(isEqualTo.key(), partitionKey), formatValue(isEqualTo.comparisonValue()));
    }

    private static String mapNotEqual(IsNotEqualTo isNotEqualTo, String partitionKey) {
        return format("%s != %s", formatKey(isNotEqualTo.key(), partitionKey), formatValue(isNotEqualTo.comparisonValue()));
    }

    private static String mapGreaterThan(IsGreaterThan isGreaterThan, String partitionKey) {
        return format("%s > %s", formatKey(isGreaterThan.key(), partitionKey), formatValue(isGreaterThan.comparisonValue()));
    }

    private static String mapGreaterThanOrEqual(IsGreaterThanOrEqualTo isGreaterThanOrEqualTo, String partitionKey) {
        return format("%s >= %s", formatKey(isGreaterThanOrEqualTo.key(), partitionKey), formatValue(isGreaterThanOrEqualTo.comparisonValue()));
    }

    private static String mapLessThan(IsLessThan isLessThan, String partitionKey) {
        return format("%s < %s", formatKey(isLessThan.key(), partitionKey), formatValue(isLessThan.comparisonValue()));
    }

    private static String mapLessThanOrEqual(IsLessThanOrEqualTo isLessThanOrEqualTo, String partitionKey) {
        return format("%s <= %s", formatKey(isLessThanOrEqualTo.key(), partitionKey), formatValue(isLessThanOrEqualTo.comparisonValue()));
    }

    private static String mapIn(IsIn isIn, String partitionKey) {
        return format("%s in %s", formatKey(isIn.key(), partitionKey), formatValues(isIn.comparisonValues()));
    }

    private static String mapNotIn(IsNotIn isNotIn, String partitionKey) {
        return format("%s not in %s", formatKey(isNotIn.key(), partitionKey), formatValues(isNotIn.comparisonValues()));
    }

    private static String mapAnd(And and, String partitionKey) {
        return format("%s and %s", map(and.left(), partitionKey), map(and.right(), partitionKey));
    }

    private static String mapNot(Not not, String partitionKey) {
        return format("not(%s)", map(not.expression(), partitionKey));
    }

    private static String mapOr(Or or, String partitionKey) {
        return format("(%s or %s)", map(or.left(), partitionKey), map(or.right(), partitionKey));
    }

    private static String formatKey(String key, String partitionKey) {
        if (StringUtils.hasLength(partitionKey) && key.equals(partitionKey)) return partitionKey;
        return "metadata[\"" + key + "\"]";
    }

    private static String formatValue(Object value) {
        if (value instanceof String || value instanceof UUID) {
            return "\"" + value + "\"";
        } else {
            return value.toString();
        }
    }

    protected static List<String> formatValues(Collection<?> values) {
        return values.stream().map(MilvusMetadataFilterMapper::formatValue).collect(toList());
    }
}

