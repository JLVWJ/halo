package com.lvwj.halo.core.threadpool;

import com.lvwj.halo.core.threadpool.support.EagerThreadPoolExecutor;
import com.lvwj.halo.core.threadpool.support.TaskQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 扩展ThreadPoolTaskExecutor
 *
 * @author lvwj
 * @date 2023-01-06 14:52
 */
@Slf4j
public class MyThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

  /**
   * 是否是恶汉线程
   */
  private final boolean eagerThread;

  private final int queueCapacity;

  private final boolean allowCoreThreadTimeOut;

  private final boolean preStartAllCoreThreads;

  public MyThreadPoolTaskExecutor(boolean eagerThread, int queueCapacity, boolean allowCoreThreadTimeOut, boolean preStartAllCoreThreads) {
    this.eagerThread = eagerThread;
    this.queueCapacity = queueCapacity;
    this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    this.preStartAllCoreThreads = preStartAllCoreThreads;
  }


  @Override
  protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
    if (eagerThread) {
      return initEagerThreadPoolExecutor(threadFactory, rejectedExecutionHandler);
    }
    return super.initializeExecutor(threadFactory, rejectedExecutionHandler);
  }

  private ExecutorService initEagerThreadPoolExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
    TaskQueue queue = new TaskQueue(queueCapacity <= 0 ? 1 : queueCapacity);
    EagerThreadPoolExecutor executor = new EagerThreadPoolExecutor(this.getCorePoolSize(), this.getMaxPoolSize(), this.getKeepAliveSeconds(), TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler);
    if (this.allowCoreThreadTimeOut) {
      executor.allowCoreThreadTimeOut(true);
    }
    if (this.preStartAllCoreThreads) {
      executor.prestartAllCoreThreads();
    }
    queue.setExecutor(executor);
    try {
      FieldUtils.writeField(this, "threadPoolExecutor", executor, true);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    return executor;
  }
}
