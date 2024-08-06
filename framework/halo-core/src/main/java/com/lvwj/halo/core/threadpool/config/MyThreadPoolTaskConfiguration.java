package com.lvwj.halo.core.threadpool.config;


import com.alibaba.ttl.threadpool.TtlExecutors;
import com.lvwj.halo.core.threadpool.MyThreadPoolTaskExecutor;
import com.lvwj.halo.core.threadpool.config.prop.AsyncProperties;
import com.lvwj.halo.core.threadpool.support.SkywalkingTaskDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-05 14:40
 */
@Slf4j
@AutoConfiguration
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({AsyncProperties.class})
@ConditionalOnProperty(prefix = AsyncProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class MyThreadPoolTaskConfiguration extends AsyncConfigurerSupport {

  @Autowired
  private AsyncProperties asyncProp;

  @Override
  @Bean("asyncExecutor")
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new MyThreadPoolTaskExecutor(asyncProp.isEagerThread(), asyncProp.getQueueCapacity(), asyncProp.isAllowCoreThreadTimeOut(), asyncProp.isPreStartAllCoreThreads());
    executor.setCorePoolSize(asyncProp.getCorePoolSize());
    executor.setMaxPoolSize(asyncProp.getMaxPoolSize());
    executor.setQueueCapacity(asyncProp.getQueueCapacity());
    executor.setKeepAliveSeconds(asyncProp.getKeepAliveSeconds());
    executor.setThreadNamePrefix(asyncProp.getThreadNamePrefix());
    executor.setAllowCoreThreadTimeOut(asyncProp.isAllowCoreThreadTimeOut());
    executor.setPrestartAllCoreThreads(asyncProp.isPreStartAllCoreThreads());
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setWaitForTasksToCompleteOnShutdown(asyncProp.isWaitForTasksToCompleteOnShutdown());
    executor.setAwaitTerminationSeconds(asyncProp.getAwaitTerminationSeconds());
    executor.initialize();
    return TtlExecutors.getTtlExecutor(executor);
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return myAsyncUncaughtExceptionHandler();
  }

  @Bean
  @ConditionalOnMissingBean
  public AsyncUncaughtExceptionHandler myAsyncUncaughtExceptionHandler() {
    return (ex, method, params) -> {
      String sb = "异步调用-" + method.getName();
      log.error(sb, ex);
    };
  }

  @Bean
  public SkywalkingTaskDecorator skywalkingTaskDecorator(){
    return new SkywalkingTaskDecorator();
  }
}
