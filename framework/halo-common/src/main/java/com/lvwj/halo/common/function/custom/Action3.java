package com.lvwj.halo.common.function.custom;

import java.util.Objects;

public interface Action3<T1, T2, T3> {
    void apply(T1 obj1, T2 obj2, T3 obj3);

    default Action3<T1, T2, T3> andThen(Action3<T1, T2, T3> after) {
        Objects.requireNonNull(after);

        return (l, r, t) -> {
            apply(l, r, t);
            after.apply(l, r, t);
        };
    }
}
