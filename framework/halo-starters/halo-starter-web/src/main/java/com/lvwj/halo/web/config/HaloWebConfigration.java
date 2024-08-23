package com.lvwj.halo.web.config;

import com.lvwj.halo.web.handler.GlobalExceptionHandler;
import com.lvwj.halo.web.i18n.I18nResponseBodyAdvice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author lvweijie
 * @date 2024年07月06日 11:08
 */
@AutoConfiguration
public class HaloWebConfigration {

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    public I18nResponseBodyAdvice i18nResponseBodyAdvice() {
        return new I18nResponseBodyAdvice();
    }
}
