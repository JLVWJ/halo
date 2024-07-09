package com.lvwj.halo.mybatisplus.config.prop;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static com.lvwj.halo.mybatisplus.config.prop.MybatisPlusExtProperties.PREFIX;

/**
 * MybatisPlus配置类
 *
 * @author lvweijie
 * @date 2023年12月08日 09:18
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class MybatisPlusExtProperties {

  public static final String PREFIX = "halo.mybatis-plus";

  /**
   * 分页最大数
   */
  private Long pageLimit = 1000L;

  /**
   * 溢出总页数后是否进行处理
   */
  protected Boolean overflow = false;

  /**
   * 拦截全表UPDATE, DELETE, SELECT
   */
  private BlockAttack blockAttack = new BlockAttack();


  @Setter
  @Getter
  public static class BlockAttack {

    private boolean enabled = true;

    /**
     * 不需要拦截全表UPDATE, DELETE, SELECT的表名
     */
    private List<String> ignoreTables = new ArrayList<>();
  }
}
