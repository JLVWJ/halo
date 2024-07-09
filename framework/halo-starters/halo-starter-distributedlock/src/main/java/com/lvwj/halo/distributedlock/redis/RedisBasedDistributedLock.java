package com.lvwj.halo.distributedlock.redis;

import com.lvwj.halo.distributedlock.BaseDistributedLock;
import com.lvwj.halo.distributedlock.DistributedLockType;
import org.redisson.api.RLock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisBasedDistributedLock extends BaseDistributedLock {
    private final RLock lock;

    public RedisBasedDistributedLock(RedissonClientPlus redissonClientPlus, DistributedLockType lockType, Object name) {
        if (name instanceof Collection && lockType == DistributedLockType.MULTI) {
            this.lock = getMultiLock(redissonClientPlus, (Collection<String>) name);
        } else if (name instanceof String) {
            if (lockType == DistributedLockType.UN_REENTRANT) {
                this.lock = redissonClientPlus.getUnReentrantLock(name.toString());
            } else if (lockType == DistributedLockType.FAIR) {
                this.lock = redissonClientPlus.getFairLock(name.toString());
            } else {
                this.lock = redissonClientPlus.getLock(name.toString());
            }
        } else {
            throw new RuntimeException("[RedisBasedDistributedLock] lock init failed: please make sure lockType is right! [suggest: Multi]");
        }
    }

    private RLock getMultiLock(RedissonClientPlus redissonClientPlus, Collection<String> lockNameList) {
        List<RLock> locks = new ArrayList<>(lockNameList.size());
        lockNameList.stream().sorted().forEach(key -> locks.add(redissonClientPlus.getLock(key)));
        return redissonClientPlus.getMultiLock(locks.toArray(new RLock[]{}));
    }

    @Override
    public void lock() {
        this.lock.lock();
    }

    @Override
    public void lock(long leaseTime, TimeUnit unit) {
        this.lock.lock(leaseTime, unit);
    }

    @Override
    public boolean tryLock() {
        return this.lock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return this.lock.tryLock(time, unit);
    }

    @Override
    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        return this.lock.tryLock(waitTime, leaseTime, unit);
    }

    @Override
    public void unlock() {
        this.lock.unlock();
    }

    @Override
    public boolean isLocked() {
        return this.lock.isLocked();
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return this.lock.isHeldByCurrentThread();
    }

    @Override
    public boolean forceUnlock() {
        return this.lock.forceUnlock();
    }
}
