package com.lvwj.halo.core.bitop.intop;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IntBitAndOp implements IntBitOp {
    private final IntBitOp[] intBitOps;

    public IntBitAndOp(IntBitOp... intBitOps) {
        this.intBitOps = intBitOps;
    }

    @Override
    public boolean match(int value) {
        if (this.intBitOps == null || this.intBitOps.length == 0) {
            return true;
        }
        return Stream.of(intBitOps)
                .allMatch(intBitOp -> intBitOp.match(value));
    }

    @Override
    public String toSqlFilter(String fieldName) {
        if (this.intBitOps == null || this.intBitOps.length == 0) {
            return "";
        }
        return Stream.of(intBitOps)
                .map(intBitOp -> intBitOp.toSqlFilter(fieldName))
                .collect(Collectors.joining(" and ", "(", ")"));
    }
}