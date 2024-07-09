package com.lvwj.halo.number.config;

import com.lvwj.halo.number.config.prop.NumberProperties;
import com.lvwj.halo.number.constant.NumberConstant;
import com.lvwj.halo.number.manager.NumberManager;
import com.lvwj.halo.number.manager.NumberSegmentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * @author lvwj
 * @date 2022-08-16 16:30
 */
@AutoConfiguration
@EnableConfigurationProperties(value = {NumberProperties.class})
public class NumberConfiguration {

  @Autowired
  private NumberProperties numberProperties;

  @PostConstruct
  public void init() {
    if (null != numberProperties) {
      if (null != numberProperties.getStep()) {
        NumberConstant.STEP = numberProperties.getStep();
      }
      if (null != numberProperties.getLoadFactor()) {
        NumberConstant.LOAD_FACTOR = numberProperties.getLoadFactor();
      }
    }
  }

  @Bean
  @ConditionalOnMissingBean
  public NumberManager segmentManager() {
    return new NumberSegmentManager();
  }
}
