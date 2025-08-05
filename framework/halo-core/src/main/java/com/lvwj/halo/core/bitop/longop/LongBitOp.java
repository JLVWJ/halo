package com.lvwj.halo.core.bitop.longop;

public interface LongBitOp {
    boolean match(long value);

    String toSqlFilter(String fieldName);

    default LongBitOp or(LongBitOp other) {
        return new LongBitOrOp(this, other);
    }

    default LongBitOp and(LongBitOp other) {
        return new LongBitAndOp(this, other);
    }

    default LongBitOp not() {
        return new LongBitNotOp(this);
    }
}