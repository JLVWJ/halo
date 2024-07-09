package com.lvwj.halo.common.function.custom;

import java.util.Objects;

public interface Action2<T1, T2> {
    void apply(T1 obj1, T2 obj2);

    default Action2<T1, T2> andThen(Action2<T1, T2> after) {
        Objects.requireNonNull(after);

        return (l, r) -> {
            apply(l, r);
            after.apply(l, r);
        };
    }
}
