package com.lvwj.halo.distributedlock;


import java.util.concurrent.TimeUnit;

public abstract class BaseDistributedLock implements IDistributedLock {

    @Override
    public void lock() {

    }

    @Override
    public void lock(long leaseTime, TimeUnit unit) {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return false;
    }

    @Override
    public boolean forceUnlock() {
        return false;
    }
}
