package com.lvwj.halo.rocketmq.config;

import com.lvwj.halo.core.threadpool.ThreadPoolCache;
import com.lvwj.halo.rocketmq.annotation.RocketMQProducer;
import com.lvwj.halo.rocketmq.producer.RocketMQProducerHelper;
import com.lvwj.halo.rocketmq.producer.RocketMQProducerInterceptor;
import com.lvwj.halo.rocketmq.producer.RocketMQProducerRegistry;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.concurrent.Executor;

/**
 * RocketMQProducerConfiguration
 *
 * @author lvweijie
 * @date 2023/11/20 21:00
 */
@ConditionalOnBean(RocketMQTemplate.class)
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
public class RocketMQProducerConfiguration {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private Environment environment;

    @Value("${halo.rocketmq.producer.threadPool.corePoolSize:3}")
    private Integer corePoolSize;

    @Value("${halo.rocketmq.producer.threadPool.maxPoolSize:6}")
    private Integer maxPoolSize;

    @Value("${halo.rocketmq.producer.threadPool.queueCapacity:6000}")
    private Integer queueCapacity;

    @Bean
    public Executor rocketMQProducerThreadPool() {
        return ThreadPoolCache.getCustomizeThreadPool("RocketMQProducerThreadPool", corePoolSize, maxPoolSize, queueCapacity);
    }

    @Bean
    public RocketMQProducerHelper rocketMQProducerHelper() {
        return new RocketMQProducerHelper(rocketMQTemplate);
    }

    @Bean
    public RocketMQProducerInterceptor rocketMQProducerInterceptor() {
        return new RocketMQProducerInterceptor(environment, rocketMQProducerHelper(), rocketMQProducerThreadPool());
    }

    @Bean
    public PointcutAdvisor rocketMQPointcutAdvisor() {
        return new DefaultPointcutAdvisor(new AnnotationMatchingPointcut(null, RocketMQProducer.class), rocketMQProducerInterceptor());
    }

    @Bean
    public RocketMQProducerRegistry rocketMQProducerRegistry() {
        return new RocketMQProducerRegistry();
    }
}
