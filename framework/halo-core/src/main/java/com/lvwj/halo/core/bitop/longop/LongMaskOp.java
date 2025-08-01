package com.lvwj.halo.core.bitop.longop;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class LongMaskOp implements LongBitOp {
    private final long mask;

    private static List<LongMaskOp> MASK_OPS;

    private LongMaskOp(long mask) {
        this.mask = mask;
    }

    public boolean isSet(long code) {
        return (code & this.mask) == this.mask;
    }

    @Override
    public boolean match(long value) {
        return isSet(value);
    }

    @Override
    public String toSqlFilter(String fieldName) {
        return "(" +
                fieldName +
                " & " +
                getMask() +
                ")" +
                "=" +
                getMask();
    }

    public long set(long value, boolean isSet) {
        if (isSet) {
            return set(value);
        } else {
            return unset(value);
        }
    }

    public long set(long value) {
        return value | this.mask;
    }

    public long unset(long value) {
        return value & ~this.mask;
    }

    /**
     * 根据 bit 位的位置，获取对应的封装实例 <br />
     * Index 从 1 开始
     *
     * @param index 下标
     */
    public static LongMaskOp getByBitIndex(int index) {
        if (index < 1 || index > MASK_OPS.size()) {
            throw new IndexOutOfBoundsException();
        }
        return MASK_OPS.get(index - 1);
    }

    public static final LongMaskOp MASK_1 = new LongMaskOp(1L);
    public static final LongMaskOp MASK_2 = new LongMaskOp(1L << 1);
    public static final LongMaskOp MASK_3 = new LongMaskOp(1L << 2);
    public static final LongMaskOp MASK_4 = new LongMaskOp(1L << 3);
    public static final LongMaskOp MASK_5 = new LongMaskOp(1L << 4);
    public static final LongMaskOp MASK_6 = new LongMaskOp(1L << 5);
    public static final LongMaskOp MASK_7 = new LongMaskOp(1L << 6);
    public static final LongMaskOp MASK_8 = new LongMaskOp(1L << 7);
    public static final LongMaskOp MASK_9 = new LongMaskOp(1L << 8);
    public static final LongMaskOp MASK_10 = new LongMaskOp(1L << 9);
    public static final LongMaskOp MASK_11 = new LongMaskOp(1L << 10);
    public static final LongMaskOp MASK_12 = new LongMaskOp(1L << 11);
    public static final LongMaskOp MASK_13 = new LongMaskOp(1L << 12);
    public static final LongMaskOp MASK_14 = new LongMaskOp(1L << 13);
    public static final LongMaskOp MASK_15 = new LongMaskOp(1L << 14);
    public static final LongMaskOp MASK_16 = new LongMaskOp(1L << 15);
    public static final LongMaskOp MASK_17 = new LongMaskOp(1L << 16);
    public static final LongMaskOp MASK_18 = new LongMaskOp(1L << 17);
    public static final LongMaskOp MASK_19 = new LongMaskOp(1L << 18);
    public static final LongMaskOp MASK_20 = new LongMaskOp(1L << 19);
    public static final LongMaskOp MASK_21 = new LongMaskOp(1L << 20);
    public static final LongMaskOp MASK_22 = new LongMaskOp(1L << 21);
    public static final LongMaskOp MASK_23 = new LongMaskOp(1L << 22);
    public static final LongMaskOp MASK_24 = new LongMaskOp(1L << 23);
    public static final LongMaskOp MASK_25 = new LongMaskOp(1L << 24);
    public static final LongMaskOp MASK_26 = new LongMaskOp(1L << 25);
    public static final LongMaskOp MASK_27 = new LongMaskOp(1L << 26);
    public static final LongMaskOp MASK_28 = new LongMaskOp(1L << 27);
    public static final LongMaskOp MASK_29 = new LongMaskOp(1L << 28);
    public static final LongMaskOp MASK_30 = new LongMaskOp(1L << 29);
    public static final LongMaskOp MASK_31 = new LongMaskOp(1L << 30);
    public static final LongMaskOp MASK_32 = new LongMaskOp(1L << 31);
    public static final LongMaskOp MASK_33 = new LongMaskOp(1L << 32);
    public static final LongMaskOp MASK_34 = new LongMaskOp(1L << 33);
    public static final LongMaskOp MASK_35 = new LongMaskOp(1L << 34);
    public static final LongMaskOp MASK_36 = new LongMaskOp(1L << 35);
    public static final LongMaskOp MASK_37 = new LongMaskOp(1L << 36);
    public static final LongMaskOp MASK_38 = new LongMaskOp(1L << 37);
    public static final LongMaskOp MASK_39 = new LongMaskOp(1L << 38);
    public static final LongMaskOp MASK_40 = new LongMaskOp(1L << 39);
    public static final LongMaskOp MASK_41 = new LongMaskOp(1L << 40);
    public static final LongMaskOp MASK_42 = new LongMaskOp(1L << 41);
    public static final LongMaskOp MASK_43 = new LongMaskOp(1L << 42);
    public static final LongMaskOp MASK_44 = new LongMaskOp(1L << 43);
    public static final LongMaskOp MASK_45 = new LongMaskOp(1L << 44);
    public static final LongMaskOp MASK_46 = new LongMaskOp(1L << 45);
    public static final LongMaskOp MASK_47 = new LongMaskOp(1L << 46);
    public static final LongMaskOp MASK_48 = new LongMaskOp(1L << 47);
    public static final LongMaskOp MASK_49 = new LongMaskOp(1L << 48);
    public static final LongMaskOp MASK_50 = new LongMaskOp(1L << 49);
    public static final LongMaskOp MASK_51 = new LongMaskOp(1L << 50);
    public static final LongMaskOp MASK_52 = new LongMaskOp(1L << 51);
    public static final LongMaskOp MASK_53 = new LongMaskOp(1L << 52);
    public static final LongMaskOp MASK_54 = new LongMaskOp(1L << 53);
    public static final LongMaskOp MASK_55 = new LongMaskOp(1L << 54);
    public static final LongMaskOp MASK_56 = new LongMaskOp(1L << 55);
    public static final LongMaskOp MASK_57 = new LongMaskOp(1L << 56);
    public static final LongMaskOp MASK_58 = new LongMaskOp(1L << 57);
    public static final LongMaskOp MASK_59 = new LongMaskOp(1L << 58);
    public static final LongMaskOp MASK_60 = new LongMaskOp(1L << 59);
    public static final LongMaskOp MASK_61 = new LongMaskOp(1L << 60);
    public static final LongMaskOp MASK_62 = new LongMaskOp(1L << 61);
    public static final LongMaskOp MASK_63 = new LongMaskOp(1L << 62);
    public static final LongMaskOp MASK_64 = new LongMaskOp(1L << 63);


    static {
        MASK_OPS = Arrays.asList(LongMaskOp.MASK_1, LongMaskOp.MASK_2, LongMaskOp.MASK_3, LongMaskOp.MASK_4, LongMaskOp.MASK_5, LongMaskOp.MASK_6, LongMaskOp.MASK_7, LongMaskOp.MASK_8, LongMaskOp.MASK_9, LongMaskOp.MASK_10,
                LongMaskOp.MASK_11, LongMaskOp.MASK_12, LongMaskOp.MASK_13, LongMaskOp.MASK_14, LongMaskOp.MASK_15, LongMaskOp.MASK_16, LongMaskOp.MASK_17, LongMaskOp.MASK_18, LongMaskOp.MASK_19, LongMaskOp.MASK_20,
                LongMaskOp.MASK_21, LongMaskOp.MASK_22, LongMaskOp.MASK_23, LongMaskOp.MASK_24, LongMaskOp.MASK_25, LongMaskOp.MASK_26, LongMaskOp.MASK_27, LongMaskOp.MASK_28, LongMaskOp.MASK_29, LongMaskOp.MASK_30,
                LongMaskOp.MASK_31, LongMaskOp.MASK_32,
                LongMaskOp.MASK_33, LongMaskOp.MASK_34, LongMaskOp.MASK_35, LongMaskOp.MASK_36, LongMaskOp.MASK_37, LongMaskOp.MASK_38, LongMaskOp.MASK_39, LongMaskOp.MASK_40, LongMaskOp.MASK_41, LongMaskOp.MASK_42,
                LongMaskOp.MASK_43, LongMaskOp.MASK_44, LongMaskOp.MASK_45, LongMaskOp.MASK_46, LongMaskOp.MASK_47, LongMaskOp.MASK_48, LongMaskOp.MASK_49, LongMaskOp.MASK_50, LongMaskOp.MASK_51, LongMaskOp.MASK_52,
                LongMaskOp.MASK_53, LongMaskOp.MASK_54, LongMaskOp.MASK_55, LongMaskOp.MASK_56, LongMaskOp.MASK_57, LongMaskOp.MASK_58, LongMaskOp.MASK_59, LongMaskOp.MASK_60, LongMaskOp.MASK_61, LongMaskOp.MASK_62,
                LongMaskOp.MASK_63, LongMaskOp.MASK_64);
    }

}