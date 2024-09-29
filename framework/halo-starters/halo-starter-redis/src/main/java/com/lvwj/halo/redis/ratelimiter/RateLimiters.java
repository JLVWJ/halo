package com.lvwj.halo.redis.ratelimiter;

import java.lang.annotation.*;

/**
 * 分布式限流注解
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-14 16:11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RateLimiters {
  RateLimiter[] value();
}
