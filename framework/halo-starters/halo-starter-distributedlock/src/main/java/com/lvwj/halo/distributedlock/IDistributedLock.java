package com.lvwj.halo.distributedlock;


import com.lvwj.halo.common.function.checked.CheckedSupplier;
import com.lvwj.halo.common.utils.Exceptions;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 *
 * @author lvweijie
 * @date 2023/11/11 17:13
 */
public interface IDistributedLock {

  void lock();

  void lock(long leaseTime, TimeUnit unit);

  boolean tryLock();

  boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

  boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;


  void unlock();

  boolean isLocked();

  boolean isHeldByCurrentThread();

  boolean forceUnlock();

  default <T> T tryLock(long waitTime, long leaseTime, TimeUnit unit, String msg, CheckedSupplier<T> supplier) {
    try {
      boolean result = tryLock(waitTime, leaseTime, unit);
      if (result) {
        //加锁成功
        try {
          return supplier.get();
        } catch (Throwable e) {
          throw Exceptions.unchecked(e);
        } finally {
          //释放锁
          this.unlock();
        }
      } else {
        throw new RuntimeException(msg);
      }
    } catch (InterruptedException e) {
      throw Exceptions.unchecked(e);
    }
  }

  default void lock(Runnable runnable) {
    this.lock();
    try {
      runnable.run();
    } finally {
      this.unlock();
    }
  }

  default void lock(long leaseTime, TimeUnit unit, Runnable runnable) {
    this.lock(leaseTime, unit);
    try {
      runnable.run();
    } finally {
      this.unlock();
    }
  }
}
