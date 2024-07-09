package com.lvwj.halo.common.function.custom;

import java.util.Objects;

public interface Action1<T> {
    void apply(T obj);

    default Action1<T> andThen(Action1<T> after) {
        Objects.requireNonNull(after);

        return l -> {
            apply(l);
            after.apply(l);
        };
    }
}
