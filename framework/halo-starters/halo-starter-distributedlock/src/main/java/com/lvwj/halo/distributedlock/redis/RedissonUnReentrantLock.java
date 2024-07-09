package com.lvwj.halo.distributedlock.redis;

import org.redisson.RedissonLock;
import org.redisson.command.CommandAsyncExecutor;

import java.util.UUID;

/**
 * 不可重入锁
 *
 * @author lvweijie
 * @date 2023年11月11日 16:38
 */
public class RedissonUnReentrantLock extends RedissonLock {
    public RedissonUnReentrantLock(CommandAsyncExecutor commandExecutor, String name) {
        super(commandExecutor, name);
    }

    /**
     * 通过返回UUID随机串，锁会认为每次获取锁都是不同线程来获取（不管实际是不是同一线程来获取），进而实现不可重入
     */
    @Override
    protected String getLockName(long threadId) {
        return UUID.randomUUID().toString();
    }

    /**
     * 释放不可重入锁，直接调forceUnlock根据锁名称来释放
     */
    @Override
    public void unlock() {
        super.forceUnlock();
    }
}
