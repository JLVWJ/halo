package com.lvwj.halo.lazyload.core;

import com.lvwj.halo.common.utils.BeanUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.Map;


/**
 * 懒加载拦截器
 *
 * @author lvweijie
 * @date 2023年11月04日 14:56
 */
public class LazyLoadInterceptor implements InvocationHandler, MethodInterceptor {
    private final Map<String, PropertyLazyLoader> lazyLoaderCache;
    private final Object target;

    public LazyLoadInterceptor(Map<String, PropertyLazyLoader> lazyLoaderCache, Object target) {
        this.target = target;
        this.lazyLoaderCache = lazyLoaderCache;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (methodInvocation instanceof ProxyMethodInvocation) {
            ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation) methodInvocation;
            return invoke(proxyMethodInvocation.getProxy(), proxyMethodInvocation.getMethod(), proxyMethodInvocation.getArguments());
        }
        return invoke(methodInvocation.getThis(), methodInvocation.getMethod(), methodInvocation.getArguments());
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Object data = method.invoke(target, objects);
        //返回值为null 且 是get方法时，走属性懒加载逻辑
        if (null == data && BeanUtil.isGetter(method)) {
            String propertyName = BeanUtil.getPropertyName(method);
            PropertyLazyLoader propertyLazyLoader = this.lazyLoaderCache.get(propertyName);
            if (propertyLazyLoader != null) {
                data = propertyLazyLoader.loadData(o);
                if (data != null) {
                    FieldUtils.writeField(target, propertyName, data, true);
                }
            }
        }
        return data;
    }
}
