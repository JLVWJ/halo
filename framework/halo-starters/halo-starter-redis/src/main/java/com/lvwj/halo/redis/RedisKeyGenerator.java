package com.lvwj.halo.redis;


import org.apache.commons.lang3.StringUtils;

/**
 * redisKey生成器
 *
 * @author lvwj
 * @date 2022-08-16 10:45
 */
public class RedisKeyGenerator {

  /**
   * 拼接key前缀
   *
   * @param bizKey 业务key
   * @return String
   */
  public static String gen(String bizKey) {
    String keyPrefix = System.getProperty("app.id");
    if (StringUtils.isBlank(keyPrefix)) {
      keyPrefix = System.getProperty("spring.application.name");
    }
    if (StringUtils.isNotBlank(keyPrefix)) {
      return String.join(":", keyPrefix, bizKey);
    }
    return bizKey;
  }
}
