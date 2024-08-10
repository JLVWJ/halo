package com.lvwj.halo.cache.core.manager.multi;

import com.lvwj.halo.redis.RedisTemplatePlus;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class HaloMultiLevelCacheManager extends AbstractTransactionSupportingCacheManager {

  private final CacheManager local;
  private final CacheManager remote;

  private final RedisTemplatePlus redisTemplatePlus;
  private boolean allowNullValues = true;

  public HaloMultiLevelCacheManager(CacheManager local, CacheManager remote, RedisTemplatePlus redisTemplatePlus) {
    this.local = local;
    this.remote = remote;
    this.redisTemplatePlus = redisTemplatePlus;
  }

  @Override
  protected Collection<? extends Cache> loadCaches() {
    return Collections.emptySet();
  }

  @Override
  public Collection<String> getCacheNames() {
    // 本地缓存和远程缓存名字相同，取其中一个即可
    Set<String> all = new HashSet<>();
    all.addAll(local.getCacheNames());
    all.addAll(remote.getCacheNames());
    return Collections.unmodifiableSet(all);
  }

  @Override
  protected Cache getMissingCache(String name) {
    Cache l1 = local.getCache(name);
    Cache l2 = remote.getCache(name);
    if(l1 == null && l2 == null) {
      return null;
    }
    return new HaloMultiLevelCache(name, l1,l2, redisTemplatePlus, allowNullValues);
  }

  public void setAllowNullValues(boolean allowNullValues) {
    this.allowNullValues = allowNullValues;
  }
}
