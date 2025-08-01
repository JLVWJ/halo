package com.lvwj.halo.core.bitop.longop;

public class LongBitNotOp implements LongBitOp {
    private final LongBitOp longBitOp;

    LongBitNotOp(LongBitOp longBitOp) {
        this.longBitOp = longBitOp;
    }

    @Override
    public boolean match(long value) {
        return !this.longBitOp.match(value);
    }

    @Override
    public String toSqlFilter(String fieldName) {

        LongMaskOp longMaskOp = (LongMaskOp) longBitOp;
        return "(" +
                fieldName +
                " & " +
                longMaskOp.getMask() +
                ")" +
                "<>" +
                longMaskOp.getMask();
    }
}