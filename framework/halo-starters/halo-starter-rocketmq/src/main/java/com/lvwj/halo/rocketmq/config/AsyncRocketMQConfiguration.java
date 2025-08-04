package com.lvwj.halo.rocketmq.config;

import com.lvwj.halo.core.threadpool.ThreadPoolCache;
import com.lvwj.halo.rocketmq.annotation.AsyncRocketMQ;
import com.lvwj.halo.rocketmq.async.AsyncRocketMQConsumerContainerRegistry;
import com.lvwj.halo.rocketmq.async.AsyncRocketMQProducerInterceptor;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;

/**
 * AsyncRocketMQConfiguration
 *
 * @author lvweijie
 * @date 2023/11/20 21:00
 */
@ConditionalOnBean(RocketMQTemplate.class)
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
public class AsyncRocketMQConfiguration {

    @Value("${halo.async.rocketmq.threadPool.corePoolSize:5}")
    private Integer corePoolSize;

    @Value("${halo.async.rocketmq.threadPool.maxPoolSize:10}")
    private Integer maxPoolSize;

    @Value("${halo.async.rocketmq.threadPool.queueCapacity:6000}")
    private Integer queueCapacity;

    @Bean
    public Executor asyncRocketMQThreadPool() {
        return ThreadPoolCache.getCustomizeThreadPool("AsyncRocketMQThreadPool", corePoolSize, maxPoolSize, queueCapacity);
    }

    @Bean
    public AsyncRocketMQConsumerContainerRegistry asyncRocketMQConsumerContainerRegistry() {
        return new AsyncRocketMQConsumerContainerRegistry();
    }

    @Bean
    public AsyncRocketMQProducerInterceptor asyncRocketMQProducerInterceptor() {
        return new AsyncRocketMQProducerInterceptor();
    }

    @Bean
    public PointcutAdvisor AsyncRocketMQPointcutAdvisor() {
        return new DefaultPointcutAdvisor(new AnnotationMatchingPointcut(null, AsyncRocketMQ.class), asyncRocketMQProducerInterceptor());
    }
}
