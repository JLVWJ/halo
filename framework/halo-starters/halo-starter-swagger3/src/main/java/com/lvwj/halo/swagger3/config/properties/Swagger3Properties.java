package com.lvwj.halo.swagger3.config.properties;

import cn.hutool.extra.spring.SpringUtil;
import com.lvwj.halo.common.constants.PropertyKeyConstant;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = Swagger3Properties.PREFIX)
public class Swagger3Properties {

    public static final String PREFIX = "halo.swagger3";

    private boolean enabled = true;

    private OpenAPIInfo info = new OpenAPIInfo();

    private SwaggerDubbo dubbo = new SwaggerDubbo();

    public Info toInfo() {
        if (this.info == null) return null;
        Info result = new Info();
        result.setTitle(info.getTitle());
        result.setDescription(info.getDescription());
        result.setVersion(info.getVersion());
        result.setSummary(info.getSummary());
        result.setExtensions(info.getExtensions());
        if (null != info.getContact()) {
            Contact contact = new Contact();
            contact.setName(info.getContact().getName());
            contact.setUrl(info.getContact().getUrl());
            contact.setEmail(info.getContact().getEmail());
            contact.setExtensions(info.getContact().getExtensions());
            result.setContact(contact);
        }
        if (null != info.getLicense()) {
            License license = new License();
            license.setName(info.getLicense().getName());
            license.setUrl(info.getLicense().getUrl());
            license.setIdentifier(info.getLicense().getIdentifier());
            license.setExtensions(info.getLicense().getExtensions());
            result.setLicense(license);
        }
        return result;
    }

    @Data
    public static class OpenAPIInfo implements Serializable {
        private String title = SpringUtil.getProperty(PropertyKeyConstant.APP_NAME) + " - API文档";
        private String description = SpringUtil.getProperty(PropertyKeyConstant.APP_NAME) + " - API文档";
        private String version = "1.0.0";
        private String termsOfService;
        private String summary;
        private OpenAPIInfoContact contact;
        private OpenAPIInfoLicense license;
        private Map<String, Object> extensions;
    }

    @Data
    public static class OpenAPIInfoContact implements Serializable {
        private String name;
        private String url;
        private String email;
        private Map<String, Object> extensions;
    }

    @Data
    public static class OpenAPIInfoLicense implements Serializable {
        private String name;
        private String url;
        private String identifier;
        private Map<String, Object> extensions;
    }


    @Data
    public static class SwaggerDubbo implements Serializable {

        public static final String CLUSTER_RPC = "rpc";

        /**
         * 是否启用swagger-dubbo，默认为true
         */
        private boolean enabled = true;

        /**
         * 文档doc path
         */
        private String doc = "swagger-dubbo";

        /**
         * http请求地址，默认为http://ip:port/h/com.XXX.XxService/method
         */
        private String http = "h";

        /**
         * rpc zk调用 or 本地调用
         */
        private String cluster = CLUSTER_RPC;

        private SwaggerDubboApplication application = new SwaggerDubboApplication();
    }

    @Data
    public static class SwaggerDubboApplication implements Serializable {
        /**
         * dubbo 服务版本号
         */
        private String version;
        /**
         * dubbo服务groupId
         */
        private String groupId;
        /**
         * dubbo服务artifactId
         */
        private String artifactId;
    }
}
