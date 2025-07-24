package com.lvwj.halo.dubbo.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = HaloDubboProperties.PREFIX)
public class HaloDubboProperties {

    public static final String PREFIX = "halo.dubbo";

    /**
     * 过滤器配置
     */
    private FilterConfigProp filter = new FilterConfigProp();


    @Data
    public class FilterConfigProp {
        private FilterLogConfigProp requestLog = new FilterLogConfigProp();
        private FilterLogConfigProp responseLog= new FilterLogConfigProp();
    }

    @Data
    public class FilterLogConfigProp {
        private Boolean enable = true;
        private List<String> excludeMethods = new ArrayList<>();
    }
}
