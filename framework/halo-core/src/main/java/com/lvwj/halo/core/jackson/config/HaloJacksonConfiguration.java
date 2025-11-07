package com.lvwj.halo.core.jackson.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwj.halo.core.jackson.builder.HaloJackson2ObjectMapperBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Jackson 自动配置类
 * 功能：自定义 Jackson 组件（ObjectMapper、消息转换器等），兼容 Spring 自动配置机制
 * 优先级：高于 Spring 默认的 JacksonAutoConfiguration，确保自定义配置生效
 */
@AutoConfiguration
@AutoConfigureBefore(JacksonAutoConfiguration.class) // 确保在 Spring 默认 Jackson 配置前生效
@ConditionalOnClass(ObjectMapper.class) // 仅当类路径存在 ObjectMapper 时生效（依赖 Jackson 时才触发）
public class HaloJacksonConfiguration {

    /**
     * 注册自定义 Jackson 构建器
     * 条件：当容器中没有 HaloJackson2ObjectMapperBuilder 时才创建
     */
    @Bean
    @ConditionalOnMissingBean
    public HaloJackson2ObjectMapperBuilder haloJackson2ObjectMapperBuilder(Environment environment) {
        return new HaloJackson2ObjectMapperBuilder(environment);
    }

    /**
     * 注册自定义 ObjectMapper（核心序列化/反序列化工具）
     * 特性：
     * 1. @Primary：确保是容器中首选的 ObjectMapper，优先于其他同名 Bean
     * 2. @ConditionalOnMissingBean：允许用户自定义 ObjectMapper 覆盖此配置
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper(HaloJackson2ObjectMapperBuilder builder) {
        return builder.build();
    }

    /**
     * 注册 HTTP 消息转换器（处理接口请求/响应的 JSON 序列化）
     * 条件：当容器中没有 MappingJackson2HttpMessageConverter 时才创建
     */
    @Bean
    @ConditionalOnMissingBean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(HaloJackson2ObjectMapperBuilder builder) {
        // 使用自定义 Builder 构建的 ObjectMapper，确保转换器使用统一配置
        return new MappingJackson2HttpMessageConverter(builder.build());
    }
}