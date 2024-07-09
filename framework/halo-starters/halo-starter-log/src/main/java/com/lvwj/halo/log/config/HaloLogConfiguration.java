package com.lvwj.halo.log.config;

import com.lvwj.halo.log.access.webflux.WebFluxAccessLogFilter;
import com.lvwj.halo.log.access.webmvc.WebMvcAccessLogFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;

/**
 * @author lvweijie
 * @date 2024年07月06日 10:05
 */
@AutoConfiguration
public class HaloLogConfiguration {


    @Bean
    public WebMvcAccessLogFilter webMvcAccessLogFilter() {
        return new WebMvcAccessLogFilter();
    }

    @Bean
    @ConditionalOnClass(HandlerMapping.class)
    public WebFluxAccessLogFilter webFluxAccessLogFilter() {
        return new WebFluxAccessLogFilter();
    }
}
