package com.lvwj.halo.elasticsearch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = HaloElasticProperties.PREFIX)
public class HaloElasticProperties {

    public static final String PREFIX = "halo.elasticsearch";

    private Boolean autoIndex = Boolean.FALSE;



}
