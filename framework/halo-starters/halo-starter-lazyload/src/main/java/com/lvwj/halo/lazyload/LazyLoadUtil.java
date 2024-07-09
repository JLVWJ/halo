package com.lvwj.halo.lazyload;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvweijie
 * @date 2023年11月24日 10:09
 */
public class LazyLoadUtil {

    private static final Map<Class<?>, Boolean> lazyLoadMap = new HashMap<>();

    /**
     * 判断类的字段是否有加@LazyLoad
     * @author lvweijie
     * @date 2023/11/24 10:13
     * @param clazz clazz
     * @return java.lang.Boolean
     */
    public static Boolean hasLazyLoad(Class<?> clazz) {
        return lazyLoadMap.computeIfAbsent(clazz, LazyLoadUtil::isExistLazyLoad);
    }

    private static boolean isExistLazyLoad(Class<?> ctxClass) {
        Field[] fieldsWithAnnotation = FieldUtils.getFieldsWithAnnotation(ctxClass, LazyLoad.class);
        return null != fieldsWithAnnotation && fieldsWithAnnotation.length > 0;
    }
}
