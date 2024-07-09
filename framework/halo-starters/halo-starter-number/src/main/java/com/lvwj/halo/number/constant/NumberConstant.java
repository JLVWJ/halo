package com.lvwj.halo.number.constant;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lvwj
 * @date 2022-08-11 16:52
 */
public class NumberConstant {

  /**
   * 缓存前缀
   */
  public static String CACHE_KEY_PREFIX = "NUMBER:";

  public static Integer STEP_MIN = 1;
  public static Integer STEP_MAX = 1000;

  /**
   * 加载步长：一次加载多少数量
   */
  public static Integer DEFAULT_STEP = 100;
  /**
   * 加载因子：号码数量少于多少百分比时，自动补充
   */
  public static Double DEFAULT_LOAD_FACTOR = 0.3;

  /**
   * 加载步长：一次加载多少数量
   */
  public static Integer STEP = DEFAULT_STEP;
  /**
   * 加载因子：号码数量少于多少百分比时，自动补充
   */
  public static Double LOAD_FACTOR = DEFAULT_LOAD_FACTOR;


  private static final ThreadFactory ASYNC_THREAD_FACTORY = new ThreadFactory() {
    final AtomicLong count = new AtomicLong(0);

    @Override
    public Thread newThread(Runnable r) {
      Thread thread = Executors.defaultThreadFactory().newThread(r);
      thread.setName("NUMBER-LOAD-" + count.incrementAndGet());
      return thread;
    }
  };

  public static final ExecutorService LOAD_POOL = new ThreadPoolExecutor(2, 5,
      10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), ASYNC_THREAD_FACTORY,
      new ThreadPoolExecutor.AbortPolicy());
}
