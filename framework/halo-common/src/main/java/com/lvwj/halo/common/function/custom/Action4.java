package com.lvwj.halo.common.function.custom;

import java.util.Objects;

public interface Action4<T1, T2, T3, T4> {
    void apply(T1 obj1, T2 obj2, T3 obj3, T4 obj4);

    default Action4<T1, T2, T3, T4> andThen(Action4<T1, T2, T3, T4> after) {
        Objects.requireNonNull(after);

        return (l, r, t, s) -> {
            apply(l, r, t, s);
            after.apply(l, r, t, s);
        };
    }
}
