package com.lvwj.halo.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwj.halo.core.convert.EnumToStringConverter;
import com.lvwj.halo.core.convert.StringToEnumConverter;
import com.lvwj.halo.web.access.webflux.WebFluxAccessLogFilter;
import com.lvwj.halo.web.access.webmvc.WebMvcAccessLogFilter;
import com.lvwj.halo.web.handler.GlobalExceptionHandler;
import com.lvwj.halo.web.i18n.I18nResponseBodyAdvice;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author lvweijie
 * @date 2024年07月06日 11:08
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class HaloWebConfiguration implements WebMvcConfigurer {

    @Resource
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    public I18nResponseBodyAdvice i18nResponseBodyAdvice() {
        return new I18nResponseBodyAdvice();
    }

    @Bean
    public WebMvcAccessLogFilter webMvcAccessLogFilter() {
        return new WebMvcAccessLogFilter();
    }

    @Bean
    @ConditionalOnClass(HandlerMapping.class)
    public WebFluxAccessLogFilter webFluxAccessLogFilter() {
        return new WebFluxAccessLogFilter();
    }


    @Override
    public void configureMessageConverters(@Lazy List<HttpMessageConverter<?>> converters) {
        converters.removeIf(
                x -> x instanceof StringHttpMessageConverter || x instanceof MappingJackson2HttpMessageConverter);
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        if (mappingJackson2HttpMessageConverter != null) {
            converters.add(mappingJackson2HttpMessageConverter);
        } else {
            converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
        }
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new EnumToStringConverter());
        registry.addConverter(new StringToEnumConverter());
    }
}
