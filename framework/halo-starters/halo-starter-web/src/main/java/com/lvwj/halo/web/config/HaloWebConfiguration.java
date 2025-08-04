package com.lvwj.halo.web.config;

import com.lvwj.halo.core.convert.EnumToStringConverter;
import com.lvwj.halo.core.convert.StringToEnumConverter;
import com.lvwj.halo.web.access.webflux.WebFluxAccessLogFilter;
import com.lvwj.halo.web.access.webmvc.WebMvcAccessLogFilter;
import com.lvwj.halo.web.handler.GlobalExceptionHandler;
import com.lvwj.halo.web.i18n.I18nResponseBodyAdvice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lvweijie
 * @date 2024年07月06日 11:08
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class HaloWebConfiguration implements WebMvcConfigurer {

   /* @Resource
    private ObjectMapper objectMapper;*/

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


/*    @Override
    public void configureMessageConverters(@Lazy List<HttpMessageConverter<?>> converters) {
        converters.removeIf(
                x -> x instanceof StringHttpMessageConverter || x instanceof MappingJackson2HttpMessageConverter);
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
    }*/

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new EnumToStringConverter());
        registry.addConverter(new StringToEnumConverter());
    }
}
