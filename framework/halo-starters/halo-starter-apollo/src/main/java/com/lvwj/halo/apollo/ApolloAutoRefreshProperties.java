package com.lvwj.halo.apollo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * apollo auto refresh properties
 *
 * @author lvwj
 * @date 2023-02-10 18:37
 */
@Data
@ConfigurationProperties(prefix = ApolloAutoRefreshProperties.PREFIX)
public class ApolloAutoRefreshProperties {

    public static final String PREFIX = "halo.apollo.auto-refresh";

    /**
     * 是否启用自动刷新
     */
    private boolean enabled = true;

    /**
     * 监听的namespace 若没配置，默认监听所有
     */
    private Set<String> namespaces;

}
