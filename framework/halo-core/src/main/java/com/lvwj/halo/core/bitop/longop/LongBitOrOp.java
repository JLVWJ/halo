package com.lvwj.halo.core.bitop.longop;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LongBitOrOp implements LongBitOp {
    private final LongBitOp[] longBitOps;

    LongBitOrOp(LongBitOp... longBitOps) {
        this.longBitOps = longBitOps;
    }

    @Override
    public boolean match(long value) {
        if (this.longBitOps == null || this.longBitOps.length == 0) {
            return true;
        }
        return Stream.of(this.longBitOps)
                .anyMatch(intBitOp -> intBitOp.match(value));
    }

    @Override
    public String toSqlFilter(String fieldName) {
        if (this.longBitOps == null || this.longBitOps.length == 0) {
            return "";
        }
        return Stream.of(this.longBitOps)
                .map(intBitOp -> intBitOp.toSqlFilter(fieldName))
                .collect(Collectors.joining(" or ", "(", ")"));
    }
}