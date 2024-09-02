package com.lvwj.halo.swagger3.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = Swagger3Properties.PREFIX)
public class Swagger3Properties {

    public static final String PREFIX = "halo.swagger3";

    private boolean enabled = true;

    private String basePackage = "com.qudian";

    private Swagger2DubboProperties dubbo = new Swagger2DubboProperties();

    @Data
    public static class Swagger2DubboProperties{

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

        private Swagger2DubboApplication application = new Swagger2DubboApplication();
    }

    @Data
    public static class Swagger2DubboApplication{
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
