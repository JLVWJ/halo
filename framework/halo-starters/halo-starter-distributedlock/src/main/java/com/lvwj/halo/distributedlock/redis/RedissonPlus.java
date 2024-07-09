package com.lvwj.halo.distributedlock.redis;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.config.Config;

/**
 * Redisson增强：新增不可重入锁
 *
 * @author lvweijie
 * @date 2023年11月11日 16:27
 */
public class RedissonPlus extends Redisson implements RedissonClientPlus {
    protected RedissonPlus(Config config) {
        super(config);
    }

    @Override
    public RLock getUnReentrantLock(String name) {
        return new RedissonUnReentrantLock(commandExecutor, name);
    }

    public static RedissonClientPlus create(Config config) {
        return new RedissonPlus(config);
    }
}
