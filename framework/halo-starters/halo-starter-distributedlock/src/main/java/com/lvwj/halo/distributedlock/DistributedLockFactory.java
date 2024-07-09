package com.lvwj.halo.distributedlock;

import com.lvwj.halo.distributedlock.redis.RedisBasedDistributedLock;
import com.lvwj.halo.distributedlock.redis.RedissonClientPlus;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 分布式锁工厂
 *
 * @author lvweijie
 * @date 2023年11月11日 17:02
 */
public class DistributedLockFactory {

    @Autowired
    private RedissonClientPlus redissonClientPlus;

    public IDistributedLock newLock(Object name, DistributedLockType lockType) {
        return new RedisBasedDistributedLock(redissonClientPlus, lockType, name);
    }
}
