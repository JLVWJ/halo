package com.lvwj.halo.core.jackson.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwj.halo.core.jackson.builder.HaloJackson2ObjectMapperBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * @author lvweijie
 * @date 2024年07月06日 15:02
 */
@AutoConfiguration
public class HaloJacksonConfigration {

    @Bean
    @Primary
    @ConditionalOnClass(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new HaloJackson2ObjectMapperBuilder().build();
    }
}
