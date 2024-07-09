package com.lvwj.halo.lazyload.core;

import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * 懒加载拦截器工厂
 *
 * @author lvweijie
 * @date 2023年11月04日 14:56
 */
public class LazyLoadInterceptorFactory {
    private final Map<Class<?>, Map<String, PropertyLazyLoader>> loaderCache = Maps.newHashMap();
    private final PropertyLazyLoaderFactory propertyLazyLoaderFactory;

    public LazyLoadInterceptorFactory(PropertyLazyLoaderFactory propertyLazyLoaderFactory) {
        this.propertyLazyLoaderFactory = propertyLazyLoaderFactory;
    }


    public LazyLoadInterceptor createFor(Class<?> cls, Object target){
        Map<String, PropertyLazyLoader>  loaders = this.loaderCache.computeIfAbsent(cls, this::createForClass);
        return new LazyLoadInterceptor(loaders, target);
    }

    private Map<String, PropertyLazyLoader> createForClass(Class<?> targetCls) {
        List<PropertyLazyLoader> propertyLazyLoaders = propertyLazyLoaderFactory.createFor(targetCls);
        if (CollectionUtils.isEmpty(propertyLazyLoaders)){
            return Collections.emptyMap();
        }
        return propertyLazyLoaders.stream()
                .filter(Objects::nonNull)
                .collect(toMap(loader -> loader.getField().getName(), Function.identity()));
    }
}
