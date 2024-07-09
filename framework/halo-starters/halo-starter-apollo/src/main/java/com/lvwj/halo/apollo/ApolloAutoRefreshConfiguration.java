package com.lvwj.halo.apollo;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.spring.boot.ApolloAutoConfiguration;
import com.ctrip.framework.apollo.spring.config.ConfigPropertySource;
import com.ctrip.framework.apollo.spring.config.ConfigPropertySourceFactory;
import com.ctrip.framework.apollo.spring.util.SpringInjector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Apollo自动刷新配置类
 *
 * @author lvwj
 * @date 2023-02-10 18:37
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(ApolloAutoRefreshProperties.class)
@AutoConfigureAfter({ApolloAutoConfiguration.class})
public class ApolloAutoRefreshConfiguration {

  @Resource
  private ApplicationContext applicationContext;

  @Resource
  private ApolloAutoRefreshProperties properties;

  @Bean
  @ConditionalOnProperty(prefix = ApolloAutoRefreshProperties.PREFIX, name = "enabled", matchIfMissing = true)
  public CommandLineRunner autoRefreshChangeListener() {
    return args -> {
      Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ConfigurationProperties.class);
      if (beanMap.isEmpty()) {
        return;
      }
      Set<String> namespaces = getAllNamespaces();
      if (namespaces.isEmpty()) {
        return;
      }
      //创建监听器
      ConfigChangeListener listener = getConfigChangeListener(beanMap);
      //命令空间注册监听器
      namespaces.forEach(s -> ConfigService.getConfig(s).addChangeListener(listener));
      log.info("Apollo auto refresh, namespaces: {}", namespaces);
    };
  }

  private Set<String> getAllNamespaces() {
    //如有配置命名空间，则只监听配置的命名空间，否则监听所有命名空间
    if (!CollectionUtils.isEmpty(properties.getNamespaces())) {
      return properties.getNamespaces();
    }
    ConfigPropertySourceFactory cpsf = SpringInjector.getInstance(ConfigPropertySourceFactory.class);
    List<ConfigPropertySource> propertySources = cpsf.getAllConfigPropertySources();
    return propertySources.stream().map(PropertySource::getName).collect(Collectors.toSet());
  }

  private ConfigChangeListener getConfigChangeListener(Map<String, Object> beanMap) {
    return event -> {
      Set<String> keys = new HashSet<>();
      beanMap.forEach((k, v) -> {
        ConfigurationProperties annotation = ClassUtils.getUserClass(v).getAnnotation(ConfigurationProperties.class);
        if (null != annotation) {
          String prefix = annotation.prefix();
          if (!StringUtils.hasText(prefix)) {
            prefix = annotation.value();
          }
          if (StringUtils.hasText(prefix)) {
            for (String key : event.changedKeys()) {
              if (key.startsWith(prefix.trim())) {
                keys.add(key);
              }
            }
          }
        }
      });
      if (!keys.isEmpty()) {
        //事件消费者：ConfigurationPropertiesRebinder [配置类(@ConfigurationProperties)无需添加@RefreshScope]
        applicationContext.publishEvent(new EnvironmentChangeEvent(keys));
      }
    };
  }
}
