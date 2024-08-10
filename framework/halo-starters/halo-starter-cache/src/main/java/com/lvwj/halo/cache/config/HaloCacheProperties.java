package com.lvwj.halo.cache.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "halo.cache")
public class HaloCacheProperties {

    private String type;

    /**
     * 是否允许存NULL，默认 true，防止缓存穿透
     */
    private Boolean allowNullValues = true;

    /**
     * 是否使用前缀
     */
    private Boolean useKeyPrefix = true;

    /**
     * 是否事务提交之后保存缓存，默认 true
     */
    private Boolean isTransactionAware = true;

    private Map<String, CacheProperties.Caffeine> caffeine = new HashMap<>();

    private Map<String, CacheProperties.Redis> redis = new HashMap<>();
}
