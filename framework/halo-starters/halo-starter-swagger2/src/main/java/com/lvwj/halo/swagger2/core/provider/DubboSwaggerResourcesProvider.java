package com.lvwj.halo.swagger2.core.provider;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.core.env.Environment;
import springfox.documentation.schema.ClassSupport;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.*;

/**
 * @author lvweijie
 * @date 2024年08月23日 15:40
 */
public class DubboSwaggerResourcesProvider {

    private final DocumentationCache documentationCache;
    private final String swagger1Url;
    private final String swagger2Url;
    @VisibleForTesting
    boolean swagger1Available;
    @VisibleForTesting
    boolean swagger2Available;

    public DubboSwaggerResourcesProvider(Environment environment, DocumentationCache documentationCache) {
        this.documentationCache = documentationCache;
        this.swagger1Url = environment.getProperty("springfox.documentation.swagger.v1.path", "/api-docs");
        this.swagger2Url = "/api-docs";
        this.swagger1Available = ClassSupport.classByName("springfox.documentation.swagger1.web.Swagger1Controller").isPresent();
        this.swagger2Available = ClassSupport.classByName("springfox.documentation.swagger2.web.Swagger2Controller").isPresent();
    }

    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();

        for (Map.Entry<String, Documentation> entry : documentationCache.all().entrySet()) {
            String swaggerGroup = entry.getKey();
            SwaggerResource swaggerResource;
            if (this.swagger1Available) {
                swaggerResource = this.resource(swaggerGroup, this.swagger1Url);
                swaggerResource.setSwaggerVersion("1.2");
                resources.add(swaggerResource);
            }

            if (this.swagger2Available) {
                swaggerResource = this.resource(swaggerGroup, this.swagger2Url);
                swaggerResource.setSwaggerVersion("2.0");
                resources.add(swaggerResource);
            }
        }
        Collections.sort(resources);
        return resources;
    }

    private SwaggerResource resource(String swaggerGroup, String baseUrl) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(swaggerGroup);
        swaggerResource.setUrl(this.swaggerLocation(baseUrl, swaggerGroup));
        return swaggerResource;
    }

    private String swaggerLocation(String swaggerUrl, String swaggerGroup) {
        String base = Optional.of(swaggerUrl).get();
        return "default".equals(swaggerGroup) ? base : base + "?group=" + swaggerGroup;
    }
}
