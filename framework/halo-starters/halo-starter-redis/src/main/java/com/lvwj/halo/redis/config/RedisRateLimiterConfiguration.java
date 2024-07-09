package com.lvwj.halo.redis.config;


import com.lvwj.halo.redis.ratelimiter.RedisRateLimiterAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基于 redis 的分布式限流配置
 *
 * @author lvweijie
 * @date 2022-12-14 16:18
 */
@AutoConfiguration
public class RedisRateLimiterConfiguration {
  @Bean
  public RedisRateLimiterAspect redisRateLimiterAspect() {
    return new RedisRateLimiterAspect();
  }
}
