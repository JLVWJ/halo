package com.lvwj.halo.dubbo.config;

import com.lvwj.halo.dubbo.metric.DubboThreadPoolMetrics;
import com.lvwj.halo.dubbo.serializer.BigNumberSerializer;
import com.lvwj.halo.dubbo.serializer.DateSerializer;
import com.lvwj.halo.dubbo.serializer.TemporalAccessorSerializer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import static org.apache.dubbo.spring.boot.util.DubboUtils.DUBBO_PREFIX;

/**
 * @author lvweijie
 * @date 2024年07月06日 16:05
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = DUBBO_PREFIX, name = "registry.address")
public class HaloDubboConfigration {

    @Bean
    @ConditionalOnBean(MeterRegistry.class)
    public DubboThreadPoolMetrics dubboThreadPoolMetrics(MeterRegistry meterRegistry) {
        return new DubboThreadPoolMetrics(meterRegistry);
    }


    @Bean
    public TemporalAccessorSerializer temporalAccessorSerializer(){
        return new TemporalAccessorSerializer();
    }

    @Bean
    public BigNumberSerializer bigNumberSerializer(){
        return new BigNumberSerializer();
    }

    @Bean
    public DateSerializer dateSerializer(){
        return new DateSerializer();
    }
}
