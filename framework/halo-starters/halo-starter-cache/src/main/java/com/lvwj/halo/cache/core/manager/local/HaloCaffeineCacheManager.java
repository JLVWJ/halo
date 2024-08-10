package com.lvwj.halo.cache.core.manager.local;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import jakarta.validation.constraints.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HaloCaffeineCacheManager extends AbstractTransactionSupportingCacheManager {

    private final Map<String, Caffeine<Object, Object>> initialCacheConfiguration;

    private Caffeine<Object, Object> caffeine = Caffeine.newBuilder();

    private CacheLoader<Object, Object> cacheLoader;

    private boolean allowNullValues = true;

    public HaloCaffeineCacheManager(Map<String, Caffeine<Object, Object>> initialCacheConfiguration) {
        this.initialCacheConfiguration = initialCacheConfiguration;
    }

    public void setCaffeine(Caffeine<Object, Object> caffeine) {
        Assert.notNull(caffeine, "caffeine is null");
        doSetCaffeine(caffeine);
    }

    public void setCaffeineSpec(CaffeineSpec caffeineSpec) {
        doSetCaffeine(Caffeine.from(caffeineSpec));
    }

    public void setCaffeineSpecification(String caffeineSpecification) {
        doSetCaffeine(Caffeine.from(caffeineSpecification));
    }

    private void doSetCaffeine(Caffeine<Object, Object> caffeine) {
        if (!ObjectUtils.nullSafeEquals(this.caffeine, caffeine)) {
            this.caffeine = caffeine;
        }
    }

    @NotNull
    @Override
    protected Collection<? extends Cache> loadCaches() {
        List<Cache> caches = new LinkedList<>();
        if(!CollectionUtils.isEmpty(initialCacheConfiguration)){
            initialCacheConfiguration.forEach((key, value) -> caches.add(createCaffeineCache(key, value)));
        }
        return caches;
    }

    protected CaffeineCache createCaffeineCache(String name, Caffeine<Object, Object> caffeine) {
        return new CaffeineCache(name, createNativeCaffeineCache(caffeine != null ? caffeine : this.caffeine), allowNullValues);
    }

    protected com.github.benmanes.caffeine.cache.Cache<Object, Object> createNativeCaffeineCache(Caffeine<Object, Object> caffeine) {
        if (this.cacheLoader != null) {
            return caffeine.build(this.cacheLoader);
        }
        else {
            return caffeine.build();
        }
    }

    @Override
    protected Cache getMissingCache(@NotNull String name) {
        return createCaffeineCache(name, null);
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public void setCacheLoader(CacheLoader<Object, Object> cacheLoader) {
        if(!ObjectUtils.nullSafeEquals(this.cacheLoader, cacheLoader)) {
            this.cacheLoader = cacheLoader;
        }
    }
}
