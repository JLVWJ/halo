package com.lvwj.halo.distributedlock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 *  分布式锁注解
 *
 * @author lvweijie
 * @date 2023/11/11 18:17
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DistributedLock {

  /**
   * 分布式锁的 key，请保持唯一性
   *
   */
  String value();

  /**
   * 分布式锁参数(可选), 支持 spring el, #读取方法参数 和 @读取spring bean
   *
   */
  String param() default "";

  /**
   * 等待锁超时时间，默认30
   *
   */
  long waitTime() default 30;

  /**
   * 自动解锁时间，自动解锁时间一定得大于方法执行时间，否则会导致锁提前释放，默认100
   *
   */
  long leaseTime() default 100;

  /**
   * 时间单位，默认为秒
   *
   */
  TimeUnit timeUnit() default TimeUnit.SECONDS;

  /**
   * 默认不可重入锁
   *
   */
  DistributedLockType type() default DistributedLockType.UN_REENTRANT;

  /**
   * 异常信息
   */
  String msg() default "获取锁等待超时，请确认超时时间";
}
