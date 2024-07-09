package com.lvwj.halo.dubbo.serializer;

import cn.hutool.extra.spring.SpringUtil;
import com.lvwj.halo.common.enums.IEnum;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvweijie
 * @date 2024年01月24日 12:25
 */
public class SerializerHolder {

    private static final Map<Class<?>, ISerializer<?>> HOLDER = new HashMap<>();

    private static Class<?> findGenericClass(Class<?> clazz, Class<?> genericClazz) {
        Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(clazz, genericClazz);
        return null == classes ? null : classes[0];
    }

    static {
        Map<String, ISerializer> beans = SpringUtil.getBeansOfType(ISerializer.class);
        if (!CollectionUtils.isEmpty(beans)) {
            for (Map.Entry<String, ISerializer> entry : beans.entrySet()) {
                Class<?> genericClass = findGenericClass(entry.getValue().getClass(), ISerializer.class);
                if (null == genericClass) continue;
                HOLDER.put(genericClass, entry.getValue());
            }
        }
    }

    public static ISerializer<?> find(Object obj) {
        Class<?> clazz = obj.getClass();
        for (Map.Entry<Class<?>, ISerializer<?>> entry : HOLDER.entrySet()) {
            if (clazz.equals(entry.getKey())
                    || clazz.getSuperclass().equals(entry.getKey())
                    || Arrays.stream(clazz.getInterfaces()).anyMatch(s->s.equals(entry.getKey()))
            ) {
                return entry.getValue();
            }
        }
        if (obj instanceof IEnum<?> && obj instanceof Enum<?>) {
            return EnumSerializer.INSTANCE;
        }
        return null;
    }
}
