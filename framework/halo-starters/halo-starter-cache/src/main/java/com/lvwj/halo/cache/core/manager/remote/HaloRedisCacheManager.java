package com.lvwj.halo.cache.core.manager.remote;

import com.lvwj.halo.common.constants.DurationConstant;
import com.lvwj.halo.common.utils.StringPool;
import com.lvwj.halo.redis.RedisKeyGenerator;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class HaloRedisCacheManager extends RedisCacheManager {

	public HaloRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
								 Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
		super(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation, initialCacheConfigurations);
	}

	@NonNull
	@Override
	protected RedisCache createRedisCache(@NonNull String name, @Nullable RedisCacheConfiguration cacheConfig) {
		//拼接缓存前缀
		name = RedisKeyGenerator.gen(name);
		//设置默认过期时间10分钟
		if(cacheConfig != null) {
			Duration ttl = cacheConfig.getTtlFunction().getTimeToLive(name, null);
			if(ttl.equals(Duration.ZERO)){
				cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(DurationConstant.SECONDS_TEN_MINUTE));
			}
		}
		//如果cacheNames格式类似"sample#100"可解析后直接指定过期时间
		if (!name.contains(StringPool.HASH)) {
			return super.createRedisCache(name, cacheConfig);
		}
		String[] cacheArray = name.split(StringPool.HASH);
		if (cacheArray.length < 2) {
			return super.createRedisCache(name, cacheConfig);
		}
		String cacheName = cacheArray[0];
		if (cacheConfig != null) {
			Duration cacheAge = DurationStyle.detectAndParse(cacheArray[1], ChronoUnit.SECONDS);
			cacheConfig = cacheConfig.entryTtl(cacheAge);
		}
		return super.createRedisCache(cacheName, cacheConfig);
	}
}
