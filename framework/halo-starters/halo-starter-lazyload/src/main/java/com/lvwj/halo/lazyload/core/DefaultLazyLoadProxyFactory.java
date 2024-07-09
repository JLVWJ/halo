package com.lvwj.halo.lazyload.core;

import com.lvwj.halo.lazyload.LazyLoadProxyFactory;
import org.springframework.aop.framework.ProxyFactory;

public class DefaultLazyLoadProxyFactory extends AbstractLazyLoadProxyFactory implements LazyLoadProxyFactory {
    private final LazyLoadInterceptorFactory lazyLoadInterceptorFactory;

    public DefaultLazyLoadProxyFactory(LazyLoadInterceptorFactory lazyLoadInterceptorFactory) {
        this.lazyLoadInterceptorFactory = lazyLoadInterceptorFactory;
    }

    @Override
    protected <T> T createProxyFor(Class<?> cls, T t) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(t);
        proxyFactory.addAdvice(this.lazyLoadInterceptorFactory.createFor(cls, t));
        return (T)proxyFactory.getProxy();
    }
}