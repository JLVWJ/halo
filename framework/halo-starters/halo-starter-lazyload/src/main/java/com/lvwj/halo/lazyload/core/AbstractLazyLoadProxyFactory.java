package com.lvwj.halo.lazyload.core;

import com.lvwj.halo.lazyload.LazyLoadProxyFactory;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Modifier;

/**
 * 懒加载代理工厂
 *
 * @author lvweijie
 * @date 2023年11月04日 14:56
 */
public abstract class AbstractLazyLoadProxyFactory implements LazyLoadProxyFactory {
    @Override
    public <T> T createProxyFor(T t) {
        if (t == null){
            return null;
        }
        // 基础类型直接返回
        Class<?> cls = t.getClass();
        if (ClassUtils.isPrimitiveOrWrapper(cls)){
            return t;
        }
        // 跳过 final 类
        if (Modifier.isFinal(cls.getModifiers())){
            return t;
        }
        return createProxyFor(cls, t);
    }

    protected abstract <T> T createProxyFor(Class<?> cls, T t);
}
