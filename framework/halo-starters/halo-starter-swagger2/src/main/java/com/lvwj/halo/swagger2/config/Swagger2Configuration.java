package com.lvwj.halo.swagger2.config;

import com.lvwj.halo.swagger2.config.properties.Swagger2Properties;
import com.lvwj.halo.swagger2.core.annotation.EnableDubboSwagger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;


/**
 * @author lvweijie
 * @date 2024年07月11日 16:48
 */
@ComponentScan(
        basePackages = {"com.lvwj.halo.swagger2.core.toolkit", "com.lvwj.halo.swagger2.core.web"}
)
@AutoConfiguration
//@EnableWebMvc
@EnableSwagger2
@EnableDubboSwagger
@EnableConfigurationProperties(value = {Swagger2Properties.class, ServerProperties.class})
@ConditionalOnProperty(prefix = Swagger2Properties.PREFIX, name = "enabled", havingValue = "true")
public class Swagger2Configuration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private Swagger2Properties swagger2Properties;

    /**
     * web接口
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("http").enable(swagger2Properties.isEnabled()).apiInfo(this.apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage(swagger2Properties.getBasePackage())).paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(this.applicationName + "接口文档").description(this.applicationName + "接口文档").version("1.0").build();
    }

    /**
     * dubbo接口
     */
    @Bean
    @Primary
    public SwaggerResourcesProvider newSwaggerResourcesProvider(Environment env, DocumentationCache documentationCache, DocumentationPluginsManager pluginsManager) {
        String docPath = swagger2Properties.getDubbo().getDoc();
        return new InMemorySwaggerResourcesProvider(env, documentationCache, pluginsManager) {

            @Override
            public List<SwaggerResource> get() {
                // 1. 调用 InMemorySwaggerResourcesProvider
                List<SwaggerResource> resources = super.get();
                // 2. 添加 swagger-dubbo 的资源地址
                SwaggerResource dubboSwaggerResource = new SwaggerResource();
                dubboSwaggerResource.setName("dubbo");
                dubboSwaggerResource.setSwaggerVersion("2.0");
                dubboSwaggerResource.setUrl("/"+docPath+"/api-docs");
                resources.add(0, dubboSwaggerResource);
                return resources;
            }
        };
    }

    /**
     *
     */
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
                    Objects.requireNonNull(field).setAccessible(true);
                    return (List)field.get(bean);
                } catch (IllegalAccessException | IllegalArgumentException var3) {
                    throw new IllegalStateException(var3);
                }
            }
        };
    }
}
