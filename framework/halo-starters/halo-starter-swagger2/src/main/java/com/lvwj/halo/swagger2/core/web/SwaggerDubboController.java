package com.lvwj.halo.swagger2.core.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.lvwj.halo.swagger2.config.properties.Swagger2Properties;
import com.lvwj.halo.swagger2.core.provider.DubboSwaggerResourcesProvider;
import com.lvwj.halo.swagger2.core.reader.Reader;
import com.lvwj.halo.swagger2.core.toolkit.DubboPropertyConfig;
import com.lvwj.halo.swagger2.core.toolkit.DubboServiceScanner;
import com.lvwj.halo.swagger2.core.toolkit.SwaggerDocCache;
import io.swagger.annotations.Api;
import io.swagger.config.SwaggerConfig;
import io.swagger.models.Swagger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.swagger.common.HostNameProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2MapperImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("${halo.swagger2.dubbo.doc:swagger-dubbo}")
@Api(hidden = true)
public class SwaggerDubboController {

    public static final String DEFAULT_URL = "/api-docs";
    private static final String HAL_MEDIA_TYPE = "application/hal+json";

    private final String hostNameOverride;
    private final DocumentationCache documentationCache;

    @Autowired
    private ServiceModelToSwagger2MapperImpl mapper;

    @Autowired
    private DubboServiceScanner dubboServiceScanner;
    @Autowired
    private DubboPropertyConfig dubboPropertyConfig;
    @Autowired
    private SwaggerDocCache swaggerDocCache;
    @Autowired
    private Swagger2Properties swagger2Properties;
    @Autowired
    private DubboSwaggerResourcesProvider dubboSwaggerResourcesProvider;

    @Autowired
    public SwaggerDubboController(Environment environment, DocumentationCache documentationCache) {
        this.hostNameOverride = environment.getProperty("springfox.documentation.swagger.v2.host", "DEFAULT");
        this.documentationCache = documentationCache;
    }

    @RequestMapping(value = DEFAULT_URL,
            method = RequestMethod.GET,
            produces = {"application/json; charset=utf-8", HAL_MEDIA_TYPE})
    @ResponseBody
    public ResponseEntity<Json> getApiList(@RequestParam(value = "group", required = false) String swaggerGroup, HttpServletRequest servletRequest) throws JsonProcessingException {
        boolean enable = swagger2Properties.getDubbo().isEnabled();
        if (!enable) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String groupName = Optional.ofNullable(swaggerGroup).orElse("default");
        Documentation documentation = this.documentationCache.documentationByGroup(groupName);
        if (documentation == null) {
            log.warn("Unable to find specification for group {}", groupName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String http = swagger2Properties.getDubbo().getHttp();
        Swagger swagger = swaggerDocCache.getSwagger();
        if (null != swagger) {
            return new ResponseEntity<>(new Json(io.swagger.util.Json.mapper().writeValueAsString(swagger)), HttpStatus.OK);
        } else {
            swagger = this.mapper.mapDocumentation(documentation);
        }

        final SwaggerConfig configurator = dubboPropertyConfig;
        if (configurator != null) {
            configurator.configure(swagger);
        }

        Map<Class<?>, Object> interfaceMapRef = dubboServiceScanner.interfaceMapRef();
        if (null != interfaceMapRef) {
            Reader.read(swagger, interfaceMapRef, http);
        }

        UriComponents uriComponents = HostNameProvider.componentsFrom(servletRequest, swagger.getBasePath());
        swagger.basePath(Strings.isNullOrEmpty(uriComponents.getPath()) ? "/" : uriComponents.getPath());
        if (Strings.isNullOrEmpty(swagger.getHost())) {
            swagger.host(this.hostName(uriComponents));
        }

        swaggerDocCache.setSwagger(swagger);
        return new ResponseEntity<>(new Json(io.swagger.util.Json.mapper().writeValueAsString(swagger)), HttpStatus.OK);
    }

    @GetMapping({"/swagger-resources"})
    public ResponseEntity<List<SwaggerResource>> swaggerResources() {
        return new ResponseEntity<>(this.dubboSwaggerResourcesProvider.get(), HttpStatus.OK);
    }


    private String hostName(UriComponents uriComponents) {
        if ("DEFAULT".equals(this.hostNameOverride)) {
            String host = uriComponents.getHost();
            int port = uriComponents.getPort();
            return port > -1 ? String.format("%s:%d", host, port) : host;
        } else {
            return this.hostNameOverride;
        }
    }
}
