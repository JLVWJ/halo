package com.lvwj.halo.common.function;

import java.util.function.Predicate;

import static java.util.stream.Stream.of;

/**
 * @author lvweijie
 * @date 2024年08月23日 10:27
 */
public interface Predicates {

    Predicate[] EMPTY_ARRAY = new Predicate[0];

    /**
     * {@link Predicate} always return <code>true</code>
     *
     * @param <T> the type to test
     * @return <code>true</code>
     */
    static <T> Predicate<T> alwaysTrue() {
        return e -> true;
    }

    /**
     * {@link Predicate} always return <code>false</code>
     *
     * @param <T> the type to test
     * @return <code>false</code>
     */
    static <T> Predicate<T> alwaysFalse() {
        return e -> false;
    }

    /**
     * a composed predicate that represents a short-circuiting logical AND of {@link Predicate predicates}
     *
     * @param predicates {@link Predicate predicates}
     * @param <T>        the type to test
     * @return non-null
     */
    static <T> Predicate<T> and(Predicate<T>... predicates) {
        return of(predicates).reduce(Predicate::and).orElseGet(Predicates::alwaysTrue);
    }

    /**
     * a composed predicate that represents a short-circuiting logical OR of {@link Predicate predicates}
     *
     * @param predicates {@link Predicate predicates}
     * @param <T>        the detected type
     * @return non-null
     */
    static <T> Predicate<T> or(Predicate<T>... predicates) {
        return of(predicates).reduce(Predicate::or).orElse(e -> true);
    }
}
