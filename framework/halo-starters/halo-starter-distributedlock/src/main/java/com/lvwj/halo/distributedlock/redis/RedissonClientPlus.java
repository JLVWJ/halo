package com.lvwj.halo.distributedlock.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * Redisson增强
 *
 * @author lvweijie
 * @date 2023年11月11日 16:34
 */
public interface RedissonClientPlus extends RedissonClient {

    RLock getUnReentrantLock(String name);
}
