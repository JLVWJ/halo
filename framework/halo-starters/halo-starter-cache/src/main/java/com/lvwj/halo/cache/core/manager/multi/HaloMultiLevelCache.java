package com.lvwj.halo.cache.core.manager.multi;

import com.lvwj.halo.cache.core.constant.CacheConstant;
import com.lvwj.halo.cache.core.message.CacheMessage;
import com.lvwj.halo.cache.core.message.CacheMessageType;
import com.lvwj.halo.common.enums.BaseErrorEnum;
import com.lvwj.halo.common.utils.Assert;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.redis.RedisTemplatePlus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多级缓存
 *
 * @author lvwj
 * @date 2022-08-18 14:40
 */
@Slf4j
public class HaloMultiLevelCache extends AbstractValueAdaptingCache {

  private final String cacheName;

  private final Cache local;
  private final Cache remote;

  private final RedisTemplatePlus redisTemplatePlus;

  private static final Map<String, Object> LOCKS = new ConcurrentHashMap<>();

  public HaloMultiLevelCache(String cacheName, Cache local, Cache remote, RedisTemplatePlus redisTemplatePlus, boolean allowNullValues) {
    super(allowNullValues);
    this.cacheName = cacheName;
    this.local = local;
    this.remote = remote;
    this.redisTemplatePlus = redisTemplatePlus;
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    Assert.notNullOrEmpty(key, BaseErrorEnum.PARAM_EMPTY_ERROR, "key");
    Assert.notNullOrEmpty(valueLoader, BaseErrorEnum.PARAM_EMPTY_ERROR, "valueLoader");
    synchronized (LOCKS.computeIfAbsent(key.toString(), s -> new Object())) {
      try {
        Object obj = lookup(key);
        if (Objects.nonNull(obj)) {
          return (T) obj;
        }
        //没找到,则调valueLoader.call
        obj = valueLoader.call();
        //放入缓存
        put(key, obj);
        return (T) obj;
      } catch (Exception e) {
        log.error("MultiLevelCache.get:" + e.getMessage(), e);
        return null;
      } finally {
        LOCKS.remove(key.toString());
      }
    }
  }

  @Override
  protected Object lookup(Object key) {
    // 先从caffeine中查找
    ValueWrapper obj = local.get(key);
    if (Objects.nonNull(obj)) {
      return obj.get(); //不用fromStoreValue，否则返回的是null，会再查数据库
    }
    //再从redis中查找
    obj = remote.get(key);
    Object result = Optional.ofNullable(obj).map(ValueWrapper::get).orElse(null);
    if (Objects.nonNull(result) || this.isAllowNullValues()) {
      local.put(key, result);
    }
    return result;
  }

  @Override
  public void put(Object key, Object value) {
    Assert.isFalse(!isAllowNullValues() && Objects.isNull(value), BaseErrorEnum.CACHE_NULL_ERROR);
    //使用toStoreValue(value)包装，解决caffeine不能存null的问题
    local.put(key, value);
    if (Objects.isNull(value)) {
      //null对象只存在caffeine中一份就够了，不用存redis了
      return;
    }
    remote.put(key, value);
    // 发布消息通知其他服务实例更新本地缓存
    publishMessage(key, value, CacheMessageType.UPDATE);
  }


  @Override
  public void evict(Object key) {
    local.evict(key);
    remote.evict(key);
    // 发布消息通知其他服务实例删除本地缓存
    publishMessage(key, null, CacheMessageType.DELETE);
  }

  @Override
  public void clear() {
    local.clear();
    remote.clear();
  }

  @Override
  public String getName() {
    return this.cacheName;
  }

  @Override
  public Object getNativeCache() {
    return this;
  }

  // 更新一级缓存
  public void updateL1Cache(Object key, Object value) {
    local.put(key, value);
  }

  // 删除一级缓存
  public void evictL1Cache(Object key) {
    local.evict(key);
  }

  private void publishMessage(Object key, Object value, CacheMessageType messageType) {
    CacheMessage cacheMassage = new CacheMessage(this.cacheName, key, value, messageType, Func.getLocalIP());
    redisTemplatePlus.convertAndSend(CacheConstant.CLEAR_LOCAL_TOPIC, cacheMassage);
  }
}
