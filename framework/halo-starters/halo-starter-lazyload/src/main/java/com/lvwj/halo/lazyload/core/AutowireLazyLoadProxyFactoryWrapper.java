package com.lvwj.halo.lazyload.core;

import com.lvwj.halo.lazyload.LazyLoadProxyFactory;
import org.springframework.context.ApplicationContext;

/**
 * 懒加载代理工厂
 *
 * @author lvweijie
 * @date 2023年11月04日 14:56
 */
public class AutowireLazyLoadProxyFactoryWrapper implements LazyLoadProxyFactory {
    private final LazyLoadProxyFactory lazyLoadProxyFactory;
    private final ApplicationContext applicationContext;

    public AutowireLazyLoadProxyFactoryWrapper(LazyLoadProxyFactory lazyLoadProxyFactory, ApplicationContext applicationContext) {
        this.lazyLoadProxyFactory = lazyLoadProxyFactory;
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T createProxyFor(T t) {
        if (t != null){
            applicationContext.getAutowireCapableBeanFactory().autowireBean(t);
        }
        return lazyLoadProxyFactory.createProxyFor(t);
    }
}