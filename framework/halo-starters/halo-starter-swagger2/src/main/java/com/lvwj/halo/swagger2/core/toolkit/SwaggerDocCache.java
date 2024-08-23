package com.lvwj.halo.swagger2.core.toolkit;

import io.swagger.models.Swagger;
import org.springframework.stereotype.Component;

@Component
public class SwaggerDocCache {

    private Swagger swagger;

    public Swagger getSwagger() {
        return swagger;
    }

    public void setSwagger(Swagger swagger) {
        this.swagger = swagger;
    }
}
