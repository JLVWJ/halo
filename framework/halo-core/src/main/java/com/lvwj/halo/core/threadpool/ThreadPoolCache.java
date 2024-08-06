package com.lvwj.halo.core.threadpool;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.lvwj.halo.core.threadpool.support.SkywalkingTaskDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池本地缓存
 *
 * @author lvweijie
 * @date 2024年03月20日 14:22
 */
@Slf4j
public class ThreadPoolCache {

    private ThreadPoolCache() {
    }

    private static final Map<String, ExecutorConfigurationSupport> cache = new HashMap<>();

    private static volatile SkywalkingTaskDecorator skywalkingTaskDecorator;

    private static SkywalkingTaskDecorator getSkywalkingTaskDecorator() {
        if (null == skywalkingTaskDecorator) {
            synchronized (ThreadPoolCache.class) {
                if (null == skywalkingTaskDecorator) {
                    skywalkingTaskDecorator = SpringUtil.getBean(SkywalkingTaskDecorator.class);
                }
            }
        }
        return skywalkingTaskDecorator;
    }

    /**
     * 获取自定义线程池
     *
     * @author lvweijie
     * @date 2024/3/20 16:02
     */
    public static Executor getCustomizeThreadPool(String uniqueKey, int coreSize, int maxSize, int queueCapacity) {
        return getCustomizeThreadPool(uniqueKey, true, coreSize, maxSize, queueCapacity, 0, false, false, true, 120);
    }

    /**
     * 获取自定义线程池
     *
     * @author lvweijie
     * @date 2024/3/20 16:02
     */
    public static Executor getCustomizeThreadPool(String uniqueKey, boolean eagerThread, int coreSize, int maxSize, int queueCapacity, int keepAliveSeconds, boolean allowCoreThreadTimeOut, boolean preStartAllCoreThreads, boolean waitForJobsToCompleteOnShutdown, int awaitTerminationSeconds) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) cache.computeIfAbsent(uniqueKey, k -> {
            ThreadPoolTaskExecutor executor = new MyThreadPoolTaskExecutor(eagerThread, queueCapacity, allowCoreThreadTimeOut, preStartAllCoreThreads);
            executor.setCorePoolSize(coreSize);
            executor.setMaxPoolSize(maxSize);
            executor.setQueueCapacity(queueCapacity);
            if (keepAliveSeconds > 0) {
                executor.setKeepAliveSeconds(keepAliveSeconds);
            }
            executor.setThreadNamePrefix(uniqueKey);
            executor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
            executor.setPrestartAllCoreThreads(preStartAllCoreThreads);
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.setWaitForTasksToCompleteOnShutdown(waitForJobsToCompleteOnShutdown);
            executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
            if (null != getSkywalkingTaskDecorator()) {
                executor.setTaskDecorator(getSkywalkingTaskDecorator());
            }
            executor.initialize();
            return executor;
        });
        return TtlExecutors.getTtlExecutor(threadPoolTaskExecutor);
    }

    /**
     * 获取定时线程池
     *
     * @author lvweijie
     * @date 2024/3/20 16:02
     */
    public static ThreadPoolTaskScheduler getScheduledThreadPool(String uniqueKey) {
        return getScheduledThreadPool(uniqueKey, 1);
    }

    /**
     * 获取定时线程池
     *
     * @author lvweijie
     * @date 2024/3/20 16:02
     */
    public static ThreadPoolTaskScheduler getScheduledThreadPool(String uniqueKey, int coreSize) {
        return (ThreadPoolTaskScheduler) cache.computeIfAbsent(uniqueKey, k -> {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(coreSize);
            scheduler.setWaitForTasksToCompleteOnShutdown(true);
            scheduler.setAwaitTerminationSeconds(120);
            scheduler.setThreadNamePrefix(uniqueKey);
            scheduler.setErrorHandler(new ScheduledErrorHandler(uniqueKey));
            scheduler.initialize();
            return scheduler;
        });
    }

    /**
     * 优雅关闭线程池
     *
     * @author lvweijie
     * @date 2024/3/20 16:02
     */
    public static void shoutDownGracefully() {
        cache.values().forEach(ExecutorConfigurationSupport::shutdown);
        cache.clear();
    }

    @Slf4j
    private static class ScheduledErrorHandler implements ErrorHandler {

        private final String key;

        public ScheduledErrorHandler(String key) {
            this.key = key;
        }

        @Override
        public void handleError(Throwable t) {
            log.error("ThreadPoolTaskScheduler handleError key:" + key, t);
        }
    }
}
