package com.lvwh.halo.batchhandler.queue;

import java.util.List;

/**
 * @author lvweijie
 * @date 2023年12月21日 15:56
 */
public abstract class AbstractBatchQueue<T> {

    public abstract void put(T t);

    public abstract void put(List<T> ts);

    public abstract void putFirst(List<T> ts);

    public abstract T take();

    public abstract List<T> take(int len);

    public abstract List<T> takeAll();

    public abstract int size();
}
