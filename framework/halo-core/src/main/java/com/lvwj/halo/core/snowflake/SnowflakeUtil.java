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
   * @return java.lang.Long 雪花ID
   * @author lvweijie
   * @date 2023/11/8 17:23
   */
  public static Long nextId() {
    return getSnowflakeGenerator().nextId();
  }

  /**
   * 获取雪花ID
   *
   * @param shardValue 分片键
   * @return java.lang.Long 雪花ID
   * @author lvweijie
   * @date 2023/11/8 17:23
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
   * @param id 雪花ID
   * @return java.time.LocalDateTime
   * @author lvweijie
   * @date 2023/11/8 17:24
   */
  public static LocalDateTime getDateTime(long id) {
    return getSnowflakeGenerator().getDateTime(id);
  }

  /**
   * 获取分片键值
   *
   * @author lvweijie
   * @date 2024/10/14 11:10
   * @param id 雪花ID
   * @return java.lang.Long
   */
  public static Long getShardValue(long id) {
    return getSnowflakeGenerator().getShardValue(id);
  }
}
