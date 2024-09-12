package com.lvwj.halo.core.utils;

import org.javers.core.Changes;
import org.javers.core.ChangesByObject;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author lvweijie
 * @date 2023年11月03日 15:50
 */
public class DiffUtil {

    private static final Javers javers = JaversBuilder.javers()
            .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();

    public static <T> Changes compareCollections(Collection<T> o, Collection<T> n, Class<T> clazz) {
        if (CollectionUtils.isEmpty(o) && CollectionUtils.isEmpty(n)) {
            return null;
        }
        Diff diff = javers.compareCollections(o, n, clazz);
        return diff.getChanges();
    }

    public static <T> Changes compare(T o, T n) {
        if (null == o && null == n) {
            return null;
        }
        Diff diff = javers.compare(o, n);
        return diff.getChanges();
    }

    public static <T> List<ChangesByObject> compareGroupByObject(T o, T n) {
        if (null == o && null == n) {
            return null;
        }
        Diff diff = javers.compare(o, n);
        return diff.groupByObject();
    }

    public static <T> List<ChangesByObject> compareGroupByObject(Collection<T> o, Collection<T> n, Class<T> clazz) {
        if (CollectionUtils.isEmpty(o) && CollectionUtils.isEmpty(n)) {
            return null;
        }
        Diff diff = javers.compareCollections(o, n, clazz);
        return diff.groupByObject();
    }
}
