package com.lvwj.halo.common.tuples;

/**
 * Represents a list of 3 typed Objects.
 *
 * @since 2.5.0
 */
public final class Tuple3<T1, T2, T3> extends Tuple {
    private static final long serialVersionUID = 8469774237154310687L;
    private final T1 v1;
    private final T2 v2;
    private final T3 v3;

    public Tuple3(T1 v1, T2 v2, T3 v3) {
        super(v1, v2, v3);

        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Tuple3(Tuple3<T1, T2, T3> tuple) {
        this(tuple.v1, tuple.v2, tuple.v3);
    }

    @Deprecated
    public T1 getFirst() {
        return v1;
    }

    @Deprecated
    public T2 getSecond() {
        return v2;
    }

    @Deprecated
    public T3 getThird() {
        return v3;
    }

    public T1 getV1() {
        return v1;
    }

    public T2 getV2() {
        return v2;
    }

    public T3 getV3() {
        return v3;
    }

    @Override
    public Tuple3<T1, T2, T3> clone() {
        return new Tuple3<>(this);
    }
}
