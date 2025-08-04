package com.lvwj.halo.cache.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.lvwj.halo.cache.core.constant.HaloCacheType;
import com.lvwj.halo.cache.core.manager.local.HaloCaffeineCacheManager;
import com.lvwj.halo.cache.core.manager.multi.HaloMultiLevelCacheManager;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.redis.RedisKeyGenerator;
import com.lvwj.halo.redis.RedisTemplatePlus;
import com.lvwj.halo.redis.config.RedisTemplateConfiguration;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 缓存自动配置
 *
 * @author lvweijie
 * @date 2024年08月09日 14:44
 */
@EnableCaching
@AutoConfiguration(after = {RedisTemplateConfiguration.class})
@ConditionalOnProperty(prefix = HaloCacheProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({CacheProperties.class, HaloCacheProperties.class})
public class HaloCacheConfiguration {

    private final CacheProperties cacheProperties;

    private final HaloCacheProperties haloCacheProperties;

    private final Caffeine<Object, Object> caffeine;

    private final CaffeineSpec caffeineSpec;

    private final CacheLoader<Object, Object> cacheLoader;

    @Lazy
    @Autowired
    private CacheManagerCustomizers cacheManagerCustomizers;

    @Resource
    private RedisTemplatePlus redisTemplatePlus;



    public HaloCacheConfiguration(CacheProperties cacheProperties,
                                 HaloCacheProperties haloCacheProperties,
                                 ObjectProvider<Caffeine<Object, Object>> caffeine,
                                 ObjectProvider<CaffeineSpec> caffeineSpec,
                                 ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        this.cacheProperties = cacheProperties;
        this.haloCacheProperties = haloCacheProperties;
        this.caffeine = caffeine.getIfAvailable();
        this.caffeineSpec = caffeineSpec.getIfAvailable();
        this.cacheLoader = cacheLoader.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheManagerCustomizers cacheManagerCustomizers(ObjectProvider<CacheManagerCustomizer<?>> customizers) {
        return new CacheManagerCustomizers(customizers.orderedStream().toList());
    }

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        CacheManager cacheManager;
        if (Objects.equals(haloCacheProperties.getType(), HaloCacheType.MULTILEVEL.getCode())) {
            cacheManager = mutillevelCacheManager(redisConnectionFactory);
        } else if (Objects.equals(haloCacheProperties.getType(), HaloCacheType.LOCAL.getCode())) {
            cacheManager = localCacheManager();
        } else {
            cacheManager = remoteCacheManager(redisConnectionFactory);
        }
        return cacheManager;
    }

    private CacheManager mutillevelCacheManager(RedisConnectionFactory redisConnectionFactory) {
        return this.cacheManagerCustomizers.customize(new HaloMultiLevelCacheManager(localCacheManager(), remoteCacheManager(redisConnectionFactory), redisTemplatePlus));
    }

    private CacheManager localCacheManager() {
        Map<String, Caffeine<Object, Object>> caffeineMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(haloCacheProperties.getCaffeine())) {
            haloCacheProperties.getCaffeine().forEach((key, value) -> caffeineMap.put(key, Caffeine.from(value.getSpec())));
        }
        HaloCaffeineCacheManager cacheManager = createCaffeineManager(caffeineMap);
        if (!haloCacheProperties.getAllowNullValues()) {
            cacheManager.setAllowNullValues(false);
        }
        if (haloCacheProperties.getIsTransactionAware()) {
            cacheManager.setTransactionAware(true);
        }
        cacheManager.afterPropertiesSet();
        return this.cacheManagerCustomizers.customize(cacheManager);
    }

    private HaloCaffeineCacheManager createCaffeineManager(Map<String, Caffeine<Object, Object>> caffeineMap) {
        HaloCaffeineCacheManager cacheManager = new HaloCaffeineCacheManager(caffeineMap);
        if (StringUtils.isNotBlank(cacheProperties.getCaffeine().getSpec())) {
            cacheManager.setCaffeineSpecification(cacheProperties.getCaffeine().getSpec());
        } else if (caffeine != null) {
            cacheManager.setCaffeine(caffeine);
        } else if (caffeineSpec != null) {
            cacheManager.setCaffeineSpec(caffeineSpec);
        }
        if (cacheLoader != null) {
            cacheManager.setCacheLoader(cacheLoader);
        }
        return cacheManager;
    }


    private CacheManager remoteCacheManager(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisCacheConfiguration> redisCacheConfigs = new HashMap<>();
        Map<String, CacheProperties.Redis> redis = haloCacheProperties.getRedis();
        if (Func.isNotEmpty(redis)) {
            redis.forEach((key, value) -> {
                RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                        .computePrefixWith(new RedisKeyGenerator())
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
                if (null != value.getTimeToLive()) {
                    config.entryTtl(value.getTimeToLive());
                }
                if (Func.isNotBlank(value.getKeyPrefix())) {
                    config.prefixCacheNameWith(value.getKeyPrefix());
                }
                if (!value.isCacheNullValues() || !haloCacheProperties.getAllowNullValues()) {
                    config.disableCachingNullValues();
                }
                if (!value.isUseKeyPrefix() || !haloCacheProperties.getUseKeyPrefix()) {
                    config.disableKeyPrefix();
                }
                redisCacheConfigs.put(key, config);
            });
        }

        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory, BatchStrategies.scan(500));
        RedisCacheManager.RedisCacheManagerBuilder cacheManagerBuilder = RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(determineRedisConfiguration())
                .withInitialCacheConfigurations(redisCacheConfigs)
                .cacheWriter(redisCacheWriter);
        if (haloCacheProperties.getIsTransactionAware()) {
            cacheManagerBuilder.transactionAware();
        }
        RedisCacheManager cacheManager = cacheManagerBuilder.build();
        cacheManager.afterPropertiesSet();
        return cacheManagerCustomizers.customize(cacheManager);
    }

    private RedisCacheConfiguration determineRedisConfiguration() {
        CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .computePrefixWith(new RedisKeyGenerator())
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }
}
