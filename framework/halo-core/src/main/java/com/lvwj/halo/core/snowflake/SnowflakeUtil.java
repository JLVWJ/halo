package com.lvwj.halo.core.snowflake;

import cn.hutool.extra.spring.SpringUtil;

import java.time.LocalDateTime;

/**
 * 雪花算法
 *
 * @author lvwj
 * @date 2022-12-01 09:39
 */
public class SnowflakeUtil {

  private static SnowflakeGenerator snowflakeGenerator;

  public static SnowflakeGenerator getSnowflakeGenerator() {
    if (null == snowflakeGenerator) {
      snowflakeGenerator = SpringUtil.getBean(SnowflakeGenerator.class);
    }
    return snowflakeGenerator;
  }

    /**
     * 获取雪花ID
     *
     * @author lvweijie
     * @date 2023/11/8 17:23
     * @return java.lang.Long 雪花ID
     */
  public static Long nextId() {
    return getSnowflakeGenerator().nextId();
  }

  /**
   * 获取雪花ID
   *
   * @author lvweijie
   * @date 2023/11/8 17:23
   * @param shardValue 分片键
   * @return java.lang.Long 雪花ID
   */
  public static Long nextId(Integer shardValue) {
    if (null == shardValue) {
      return nextId();
    }
    return getSnowflakeGenerator().nextId(shardValue);
  }

  /**
   * 获取雪花ID的时间
   *
   * @author lvweijie
   * @date 2023/11/8 17:24
   * @param id  雪花ID
   * @return java.time.LocalDateTime
   */
  public static LocalDateTime getTime(long id) {
    return getSnowflakeGenerator().getDateTime(id);
  }
}
