package com.lvwj.halo.dubbo.metric;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.threadpool.manager.DefaultExecutorRepository;
import org.apache.dubbo.common.threadpool.manager.ExecutorRepository;
import org.apache.dubbo.config.spring.context.event.ServiceBeanExportedEvent;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class DubboThreadPoolMetrics implements ApplicationListener<ServiceBeanExportedEvent> {
    private MeterRegistry meterRegistry;
    private final AtomicBoolean INIT = new AtomicBoolean(false);

    public DubboThreadPoolMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void onApplicationEvent(ServiceBeanExportedEvent event) {
        init();
    }

    private void init() {
        if (!INIT.compareAndSet(false, true)) {
            return;
        }
        try {
            ExecutorRepository executorRepository = ExtensionLoader.getExtensionLoader(ExecutorRepository.class).getDefaultExtension();
            if (!(executorRepository instanceof DefaultExecutorRepository)) {
                return;
            }
            DefaultExecutorRepository defaultExecutorRepository = (DefaultExecutorRepository) executorRepository;

            Field dataFiled = defaultExecutorRepository.getClass().getDeclaredField("data");
            dataFiled.setAccessible(true);
            ConcurrentMap<String, ConcurrentMap<Integer, ExecutorService>> executorMap = (ConcurrentMap<String, ConcurrentMap<Integer, ExecutorService>>) dataFiled.get(executorRepository);
            ConcurrentMap<Integer, ExecutorService> executors = executorMap.get("java.util.concurrent.ExecutorService");

            executors.forEach((port, executor) -> {
                Tags tags = Tags.of("thread.pool.name", "dubboThreadPool", "port", Integer.toString(port));
                if (!(executor instanceof ThreadPoolExecutor)) {
                    return;
                }
                // 也可以通过microMeter自带的JVM线程池绑定器ExecutorServiceMetrics去绑定dubbo的线程池
                ThreadPoolExecutor tp = (ThreadPoolExecutor) executor;
                // prometheus会将指标转为自己的命名风格：dubbo_thread_pool_core_size
                Gauge.builder("dubbo.thread.pool.core.size", tp, ThreadPoolExecutor::getCorePoolSize)
                        .description("核心线程数")
                        .tags(tags)
                        .register(meterRegistry);
                Gauge.builder("dubbo.thread.pool.largest.size", tp, ThreadPoolExecutor::getLargestPoolSize)
                        .description("历史最高线程数")
                        .tags(tags)
                        .register(meterRegistry);
                Gauge.builder("dubbo.thread.pool.max.size", tp, ThreadPoolExecutor::getMaximumPoolSize)
                        .description("最大线程数")
                        .tags(tags)
                        .register(meterRegistry);
                Gauge.builder("dubbo.thread.pool.active.size", tp, ThreadPoolExecutor::getActiveCount)
                        .description("活跃线程数")
                        .tags(tags)
                        .register(meterRegistry);
                Gauge.builder("dubbo.thread.pool.thread.count", tp, ThreadPoolExecutor::getPoolSize)
                        .description("当前线程数")
                        .tags(tags)
                        .register(meterRegistry);
                Gauge.builder("dubbo.thread.pool.queue.size", tp, e -> e.getQueue().size())
                        .description("队列大小")
                        .tags(tags)
                        .register(meterRegistry);
                Gauge.builder("dubbo.thread.pool.taskCount", tp, ThreadPoolExecutor::getTaskCount)
                        .description("任务总量")
                        .tags(tags)
                        .register(meterRegistry);
                Gauge.builder("dubbo.thread.pool.completedTaskCount", tp, ThreadPoolExecutor::getCompletedTaskCount)
                        .description("已完成的任务量")
                        .tags(tags)
                        .register(meterRegistry);
            });
        } catch (Exception e) {
            log.error("[metric:dubbo_thread]occurs exception", e);
        }
    }
}
