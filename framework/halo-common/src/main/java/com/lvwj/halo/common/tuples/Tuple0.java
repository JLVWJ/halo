package com.lvwj.halo.common.tuples;

/**
 * Represents a list of 0 typed Object.
 *
 * @since 3.0.0
 */
public final class Tuple0 extends Tuple {
    private static final long serialVersionUID = -3791115121904072346L;
    public static final Tuple0 INSTANCE = new Tuple0();

    private Tuple0() {}

    @Override
    public Tuple0 clone() {
        return INSTANCE;
    }
}
