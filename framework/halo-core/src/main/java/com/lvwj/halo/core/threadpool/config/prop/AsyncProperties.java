package com.lvwj.halo.core.threadpool.config.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Async注解默认线程池配置
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-05 13:56
 */
@Getter
@Setter
@ConfigurationProperties(AsyncProperties.PREFIX)
public class AsyncProperties {

  public static final String PREFIX = "halo.async";

  private boolean enabled = true;
  /**
   * 是否启用恶汉线程(当达到核心线程数时，直接创建非核心线程，当达到最大线程数时，再把任务放到阻塞队列)
   */
  private boolean eagerThread = true;
  /**
   * 异步核心线程数
   */
  private int corePoolSize = Runtime.getRuntime().availableProcessors() * 3;
  /**
   * 异步最大线程数
   */
  private int maxPoolSize = corePoolSize * 5;
  /**
   * 队列容量，默认：10000
   */
  private int queueCapacity = 10000;
  /**
   * 线程存活时间，默认：300
   */
  private int keepAliveSeconds = 300;
  /**
   * 线程名前缀
   */
  private String threadNamePrefix = "HALO-ASYNC-";

  /**
   * 是否允许核心线程超时自动销毁
   */
  private boolean allowCoreThreadTimeOut = false;
  /**
   * 是否允许提前开启所有核心线程
   */
  private boolean preStartAllCoreThreads = false;

  /**
   * 关闭时 是否等待异步任务完成
   */
  private boolean waitForTasksToCompleteOnShutdown = true;

  /**
   * 等待时间(秒)
   */
  private int awaitTerminationSeconds = 120;

}
