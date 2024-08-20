package com.lvwh.halo.batchhandler.queue;


import java.util.ArrayList;
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
    public List<T> take(long len) {
        if (len <= 0) {
            len = 1;
        }
        List<T> list = new ArrayList<>((int) len);
        queue.drainTo(list, (int) len);
        return list;
    }

    @Override
    public int size() {
        return queue.size();
    }
}
