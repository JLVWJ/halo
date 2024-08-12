package com.lvwj.halo.redis;


import com.lvwj.halo.common.utils.StringPool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.cache.CacheKeyPrefix;

/**
 * redisKey生成器
 *
 * @author lvwj
 * @date 2022-08-16 10:45
 */
public class RedisKeyGenerator implements CacheKeyPrefix {

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
      return String.join(StringPool.COLON, keyPrefix, bizKey);
    }
    return bizKey;
  }

  @Override
  public String compute(String cacheName) {
    return gen(cacheName) + StringPool.COLON;
  }
}
