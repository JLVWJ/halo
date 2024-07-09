package com.lvwj.halo.common.tuples;

/**
 * Represents a list of 2 typed Objects.
 */
public final class Tuple2<T1, T2> extends Tuple {
    private static final long serialVersionUID = 9006144674906325597L;
    private final T1 v1;
    private final T2 v2;

    public Tuple2(T1 v1, T2 v2) {
        super(v1, v2);

        this.v1 = v1;
        this.v2 = v2;
    }

    public Tuple2(Tuple2<T1, T2> tuple) {
        this(tuple.v1, tuple.v2);
    }

    @Deprecated
    public T1 getFirst() {
        return v1;
    }

    @Deprecated
    public T2 getSecond() {
        return v2;
    }

    public T1 getV1() {
        return v1;
    }

    public T2 getV2() {
        return v2;
    }

    @Override
    public Tuple2<T1, T2> clone() {
        return new Tuple2<>(this);
    }
}
