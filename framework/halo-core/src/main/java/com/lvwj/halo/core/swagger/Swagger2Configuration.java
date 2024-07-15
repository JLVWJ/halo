package com.lvwj.halo.core.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author lvweijie
 * @date 2024年07月11日 16:48
 */
@AutoConfiguration
@EnableSwagger2
public class Swagger2Configuration {

    @Value("${halo.swagger.enabled:true}")
    private boolean enabled = true;
    @Value("${halo.swagger.basePackage:com.qudian}")
    private String basePackage;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public Docket createRestApi() {
        return (new Docket(DocumentationType.SWAGGER_2)).enable(this.enabled).apiInfo(this.apiInfo()).select().apis(RequestHandlerSelectors.basePackage(basePackage)).paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return (new ApiInfoBuilder()).title(this.applicationName + "接口文档").description(this.applicationName + "接口文档").version("1.0").build();
    }
}
