package com.lvwj.halo.number.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lvwj
 * @date 2022-08-11 15:34
 */
@Data
@ConfigurationProperties(prefix = "halo.number")
public class NumberProperties {

  /**
   * 步长
   */
  private Integer step;
  /**
   * 加载因子
   */
  private Double loadFactor;

}
