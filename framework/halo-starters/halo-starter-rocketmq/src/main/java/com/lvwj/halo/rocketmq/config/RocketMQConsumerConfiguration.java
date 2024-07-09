package com.lvwj.halo.rocketmq.config;

import com.lvwj.halo.rocketmq.consumer.RocketMQConsumerRegistry;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQConsumerConfiguration
 *
 * @author lvweijie
 * @date 2023/11/20 21:00
 */
@ConditionalOnBean(RocketMQTemplate.class)
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
public class RocketMQConsumerConfiguration {

    @Bean
    public RocketMQConsumerRegistry rocketMQConsumerRegistry() {
        return new RocketMQConsumerRegistry();
    }
}
