package com.lvwj.halo.common.enums;

import java.util.Objects;

/**
 * 枚举接口
 *
 * @author lvweijie
 * @date 2024年06月09日 15:50
 */
public interface IEnum<T> {

    /**
     * 枚举编码
     */
    T getCode();

    /**
     * 枚举描述
     */
    String getDescription();

    static <E extends IEnum<C>, C> E byCode(Class<E> clazz, C code) {
        if (code == null || !clazz.isEnum()) {
            return null;
        }
        E[] enums = clazz.getEnumConstants();
        for (E e : enums) {
            if (Objects.equals(e.getCode(), code)) {
                return e;
            }
        }
        return null;
    }

    static <E extends IEnum<?>> E byDescription(Class<E> clazz, String description) {
        if (null == description || description.isEmpty() || !clazz.isEnum()) {
            return null;
        }
        E[] enums = clazz.getEnumConstants();
        for (E e : enums) {
            if (Objects.equals(e.getDescription(), description)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 对比当前枚举对象和传入的枚举值是否一致（String类型会忽略大小写）
     *
     * @param enumCode 枚举code
     * @return 是否匹配
     */
    default boolean codeEquals(T enumCode) {
        if (enumCode == null) {
            return false;
        }
        if (enumCode instanceof String) {
            return ((String) enumCode).equalsIgnoreCase((String) getCode());
        } else {
            return Objects.equals(this.getCode(), enumCode);
        }
    }

    /**
     * 对比两个枚举项是否完全相同
     *
     * @param anotherEnum 枚举
     * @return 是否相同
     */
    default boolean equals(IEnum<T> anotherEnum) {
        return this == anotherEnum;
    }
}
