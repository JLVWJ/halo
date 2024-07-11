package com.lvwj.halo.web.config;

import com.lvwj.halo.web.handler.GlobalExceptionHandler;
import com.lvwj.halo.web.i18n.I18nResponseBodyAdvice;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

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

    @Bean
    public BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider) {
                    this.customizeSpringfoxHandlerMappings(this.getHandlerMappings(bean));
                }

                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                List<T> copy = mappings.stream().filter((mapping) -> mapping.getPatternParser() == null).toList();
                mappings.clear();
                mappings.addAll(copy);
            }

            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List)field.get(bean);
                } catch (IllegalAccessException | IllegalArgumentException var3) {
                    throw new IllegalStateException(var3);
                }
            }
        };
    }
}
