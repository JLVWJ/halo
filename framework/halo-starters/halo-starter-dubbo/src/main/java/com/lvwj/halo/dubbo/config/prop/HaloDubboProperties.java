package com.lvwj.halo.dubbo.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;

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

        private Boolean requestLogEnable = true;
        private Boolean responseLogEnable = true;

        private ArrayList<String> requestLogExcludeFacadeMethods = new ArrayList<>();
        private ArrayList<String> responseLogExcludeFacadeMethods = new ArrayList<>();
    }
}
