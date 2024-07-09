package com.lvwj.halo.redis.idempotent;


import com.lvwj.halo.redis.idempotent.key.ExpressionIdempotentKey;
import com.lvwj.halo.redis.idempotent.key.IdempotentKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 幂等注解
 *
 * @author lvwj
 * @date 2022-08-17 19:37
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

  /**
   * 幂等的超时时间，默认为 2 秒
   * <p>
   * 注意，如果执行时间超过它，请求还是会进来
   */
  int timeout() default 2;

  /**
   * 时间单位，默认为 SECONDS 秒
   */
  TimeUnit timeUnit() default TimeUnit.SECONDS;

  /**
   * 使用的 Key 解析器
   */
  Class<? extends IdempotentKey> keyResolver() default ExpressionIdempotentKey.class;

  String spEl() default "";
}
