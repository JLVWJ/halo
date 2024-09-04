package com.lvwj.halo.swagger3.config;

import com.lvwj.halo.swagger3.config.properties.Swagger3Properties;
import io.swagger.v3.oas.models.OpenAPI;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author lvweijie
 * @date 2024年09月02日 14:26
 */
@AutoConfiguration
@EnableConfigurationProperties(value = {Swagger3Properties.class})
public class Swagger3Configration {

    @Resource
    private Swagger3Properties swagger3Properties;


    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.setInfo(swagger3Properties.toInfo());
        return openAPI;
    }
}
