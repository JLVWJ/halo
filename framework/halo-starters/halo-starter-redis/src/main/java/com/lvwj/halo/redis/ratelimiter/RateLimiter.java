package com.lvwj.halo.redis.ratelimiter;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式限流注解，默认速率为 10次/1秒
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-14 16:11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RateLimiter {

  /**
   * 限流的 key 支持，必须：请保持唯一性
   *
   * @return key
   */
  String value();

  /**
   * 限流的参数，可选，支持 spring el # 读取方法参数和 @ 读取 spring bean
   *
   * @return param
   */
  String param() default "";

  /**
   * 支持的最大请求，默认: 10
   *
   * @return 请求数
   */
  long max() default 10L;

  /**
   * 持续时间，默认: 1
   *
   * @return 持续时间
   */
  long ttl() default 1L;

  /**
   * 时间单位，默认为分
   *
   * @return TimeUnit
   */
  TimeUnit timeUnit() default TimeUnit.SECONDS;
}
