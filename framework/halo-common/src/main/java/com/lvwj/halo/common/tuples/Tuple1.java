package com.lvwj.halo.common.tuples;

/**
 * Represents a list of 1 typed Object.
 *
 * @since 2.5.0
 */
public final class Tuple1<T1> extends Tuple {
    private static final long serialVersionUID = -4647790147461409603L;
    private final T1 v1;

    public Tuple1(T1 t1) {
        super(t1);

        this.v1 = t1;
    }

    public Tuple1(Tuple1<T1> tuple) {
        this(tuple.v1);
    }

    @Deprecated
    public T1 getFirst() {
        return v1;
    }

    public T1 getV1() {
        return v1;
    }

    @Override
    public Tuple1<T1> clone() {
        return new Tuple1<>(this);
    }
}
