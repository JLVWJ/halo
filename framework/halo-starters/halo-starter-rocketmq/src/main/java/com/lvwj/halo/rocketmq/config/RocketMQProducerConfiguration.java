package com.lvwj.halo.rocketmq.config;

import com.lvwj.halo.rocketmq.annotation.RocketMQProducer;
import com.lvwj.halo.rocketmq.producer.RocketMQProducerHelper;
import com.lvwj.halo.rocketmq.producer.RocketMQProducerInterceptor;
import com.lvwj.halo.rocketmq.producer.RocketMQProducerRegistry;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * RocketMQProducerConfiguration
 *
 * @author lvweijie
 * @date 2023/11/20 21:00
 */
@ConditionalOnBean(RocketMQTemplate.class)
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
public class RocketMQProducerConfiguration {

    @Autowired
    private RocketMQProducerInterceptor rocketMQProducerInterceptor;

    @Bean
    public RocketMQProducerHelper rocketMQProducerHelper() {
        return new RocketMQProducerHelper();
    }

    @Bean
    public RocketMQProducerInterceptor rocketMQProducerInterceptor() {
        return new RocketMQProducerInterceptor();
    }

    @Bean
    public PointcutAdvisor rocketMQPointcutAdvisor() {
        return new DefaultPointcutAdvisor(new AnnotationMatchingPointcut(null, RocketMQProducer.class), rocketMQProducerInterceptor);
    }

    @Bean
    public RocketMQProducerRegistry rocketMQProducerRegistry() {
        return new RocketMQProducerRegistry();
    }
}
