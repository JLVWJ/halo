package com.lvwh.halo.batchhandler.queue;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author lvweijie
 * @date 2023年12月21日 16:22
 */
public class MemoryBatchQueue<T> extends AbstractBatchQueue<T> {

    private final LinkedBlockingDeque<T> queue = new LinkedBlockingDeque<>();

    @Override
    public void put(T t) {
        if (null == t) {
            return;
        }
        queue.add(t);
    }

    @Override
    public void put(List<T> ts) {
        if (null == ts || ts.isEmpty()) {
            return;
        }
        queue.addAll(ts);
    }

    @Override
    public void putFirst(List<T> ts) {
        if (null == ts || ts.isEmpty()) {
            return;
        }
        ts.forEach(queue::addFirst);
    }

    @Override
    public T take() {
        return queue.poll();
    }

    @Override
    public List<T> take(int len) {
        if (len <= 0) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(len);
        queue.drainTo(list, len);
        return list;
    }

    @Override
    public List<T> takeAll() {
        return take(size());
    }

    @Override
    public int size() {
        return queue.size();
    }
}
