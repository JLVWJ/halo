package com.lvwj.halo.dubbo.serializer;

import com.lvwj.halo.common.enums.IEnum;

/**
 * 自定义枚举序列化：只返回Code
 *
 * @author lvweijie
 * @date 2024年01月24日 12:21
 */
public class EnumSerializer implements ISerializer<IEnum<?>> {

    public static final EnumSerializer INSTANCE = new EnumSerializer();

    @Override
    public Object serialize(Object t) {
        if (t instanceof IEnum<?> && t instanceof Enum<?>) {
            return ((IEnum<?>) t).getCode();
        }
        return t;
    }
}
