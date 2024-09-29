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
public @interface RateLimiter {

  /**
   * 限流开关，支持${}
   */
  String enable() default "true";

  /**
   * 限流的 key 支持，必须：请保持唯一性
   */
  String value();

  /**
   * 限流的参数，可选，支持 spring el # 读取方法参数和 @ 读取 spring bean
   */
  String param() default "";

  /**
   * 支持的最大请求,支持${}
   */
  String max() default "";

  /**
   * 持续时间,支持${}
   */
  String ttl() default "";

  /**
   * 时间单位,支持${}
   */
  String timeUnit() default "";
}
