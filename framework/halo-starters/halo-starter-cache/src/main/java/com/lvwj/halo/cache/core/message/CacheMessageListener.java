package com.lvwj.halo.cache.core.message;

import com.lvwj.halo.cache.core.manager.multi.HaloMultiLevelCache;
import com.lvwj.halo.cache.core.manager.multi.HaloMultiLevelCacheManager;
import com.lvwj.halo.common.utils.Func;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;


/**
 * 缓存消息接收处理器
 *
 * @author lvwj
 * @date 2022-08-18 14:45
 */
@Slf4j
@AllArgsConstructor
public class CacheMessageListener implements MessageListener {

  private final RedisSerializer<Object> redisSerializer;
  private final HaloMultiLevelCacheManager multiLevelCacheManager;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    if (Func.isEmpty(message)) {
      return;
    }
    CacheMessage msg = (CacheMessage) redisSerializer.deserialize(message.getBody());
    if (null == msg) {
      return;
    }
    String localIP = Func.getLocalIP();
    if (Func.isNotBlank(msg.getIp()) && msg.getIp().equals(localIP)) {
      log.info(String.format("收到本机IP[%s]发出的消息，不做处理", localIP));
      return;
    }

    HaloMultiLevelCache cache = (HaloMultiLevelCache) multiLevelCacheManager.getCache(msg.getName());
    if (null == cache) {
      return;
    }
    if (msg.getType() == CacheMessageType.UPDATE) {
      cache.updateL1Cache(msg.getKey(), msg.getValue());
      log.info(String.format("更新IP[%s]的本地缓存",localIP));
    }

    if (msg.getType() == CacheMessageType.DELETE) {
      cache.evictL1Cache(msg.getKey());
      log.info(String.format("删除IP[%s]的本地缓存",localIP));
    }
  }
}
