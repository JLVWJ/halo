package com.lvwj.halo.lazyload;


import com.lvwj.halo.lazyload.core.AutowireLazyLoadProxyFactoryWrapper;
import com.lvwj.halo.lazyload.core.DefaultLazyLoadProxyFactory;
import com.lvwj.halo.lazyload.core.LazyLoadInterceptorFactory;
import com.lvwj.halo.lazyload.core.PropertyLazyLoaderFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 懒加载配置类
 *
 * @author lvweijie
 * @date 2023年11月04日 14:56
 */
@AutoConfiguration
public class LazyLoadConfiguration {

    @Bean
    public LazyLoadProxyFactory lazyLoadProxyFactory(LazyLoadInterceptorFactory lazyLoadInterceptorFactory,
                                                     ApplicationContext applicationContext) {
        LazyLoadProxyFactory lazyLoadProxyFactory = new DefaultLazyLoadProxyFactory(lazyLoadInterceptorFactory);
        return new AutowireLazyLoadProxyFactoryWrapper(lazyLoadProxyFactory, applicationContext);
    }

    @Bean
    public LazyLoadInterceptorFactory lazyLoadInterceptorFactory(PropertyLazyLoaderFactory propertyLazyLoaderFactory) {
        return new LazyLoadInterceptorFactory(propertyLazyLoaderFactory);
    }
    @Bean
    public PropertyLazyLoaderFactory propertyLazyLoaderFactory(ApplicationContext applicationContext) {
        return new PropertyLazyLoaderFactory(applicationContext);
    }
}
