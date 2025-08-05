package com.lvwj.halo.core.bitop.intop;

public interface IntBitOp {
    boolean match(int value);

    String toSqlFilter(String fieldName);

    default IntBitOp or(IntBitOp other) {
        return new IntBitOrOp(this, other);
    }

    default IntBitOp and(IntBitOp other) {
        return new IntBitAndOp(this, other);
    }

    default IntBitOp not() {
        return new IntBitNotOp(this);
    }
}