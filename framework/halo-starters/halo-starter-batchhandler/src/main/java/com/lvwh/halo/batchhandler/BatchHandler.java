package com.lvwh.halo.batchhandler;

import com.lvwh.halo.batchhandler.queue.AbstractBatchQueue;
import com.lvwh.halo.batchhandler.queue.MemoryBatchQueue;
import com.lvwh.halo.batchhandler.queue.RedisBatchQueue;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.StringPool;
import com.lvwj.halo.core.threadpool.ThreadPoolCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author lvweijie
 * @date 2023年12月21日 16:27
 */
@Slf4j
public class BatchHandler<T extends Serializable> {

    private final List<AbstractBatchQueue<T>> queueList;

    private final Map<Integer, List<Integer>> threadQueueMap;

    private final Map<Long, Integer> threadIdMap = new HashMap<>();

    private final Consumer<List<T>> consumer;

    private final ThreadPoolTaskScheduler scheduledThreadPool;

    private final Object lock = new Object();

    private final int threshHold;
    private final int threadSize;
    private final int queueSize;


    public BatchHandler(String key, BatchHandlerType type, int threshHold, long interval, int queueSize, int threadSize, Consumer<List<T>> consumer) {
        threshHold = threshHold <= 1 ? BatchHandlerConstant.DEFAULT_THRESH_HOLD : threshHold;
        interval = interval <= 1 ? BatchHandlerConstant.DEFAULT_INTERVAL : interval;
        this.queueSize = queueSize < 0 ? 1 : queueSize;
        this.threadSize = threadSize < 0 ? 1 : threadSize;
        this.threadQueueMap = threadQueueMap();
        this.queueList = IntStream.range(0, this.queueSize).mapToObj(i -> BatchHandlerType.REDIS.equals(type) ? new RedisBatchQueue<T>(key + StringPool.COLON + i) : new MemoryBatchQueue<T>()).collect(Collectors.toList());
        this.threshHold = threshHold;
        this.consumer = consumer;
        this.scheduledThreadPool = ThreadPoolCache.getScheduledThreadPool(key, this.threadSize);
        this.scheduledThreadPool.scheduleWithFixedDelay(this::batchHandle, Duration.ofMillis(interval));
    }

    private Map<Integer, List<Integer>> threadQueueMap() {
        Map<Integer, List<Integer>> map = new HashMap<>();
        if (this.queueSize < this.threadSize) { //队列比线程数少，则多线程消费所有队列
            List<Integer> list = IntStream.range(0, this.queueSize).boxed().toList();
            IntStream.range(0, this.threadSize).forEach(s -> map.put(s, list));
        } else { //反之，则每个线程均摊消费相应队列
            int b = this.queueSize / this.threadSize;
            for (int i = 0; i < this.threadSize; i++) {
                List<Integer> list = map.computeIfAbsent(i, k -> new ArrayList<>());
                for (int j = i * b; j < b * (i + 1); j++) {
                    list.add(j);
                }
            }
            int c = 0;
            for (int i = b * this.threadSize; i < this.queueSize; i++) {
                List<Integer> list = map.get(c++);
                list.add(i);
            }
        }
        return map;
    }

    private List<AbstractBatchQueue<T>> getQueueByThreadId() {
        if (this.queueSize < this.threadSize) {
            return this.queueList;
        } else {
            long threadId = Thread.currentThread().getId();
            if (!this.threadIdMap.containsKey(threadId)) {
                synchronized (lock) {
                    if (!this.threadIdMap.containsKey(threadId)) {
                        this.threadIdMap.put(threadId, this.threadIdMap.size());
                    }
                }
            }
            Integer index = this.threadIdMap.get(threadId);
            List<Integer> queueIndexList = this.threadQueueMap.get(index);
            return queueIndexList.stream().map(this.queueList::get).toList();
        }
    }

    private void batchHandle() {
        try {
            List<T> ts = new ArrayList<>();
            List<AbstractBatchQueue<T>> queues = getQueueByThreadId();
            queues.forEach(q -> ts.addAll(q.take(this.threshHold)));
            if (CollectionUtils.isEmpty(ts)) {
                return;
            }
            consumer.accept(ts);
            //log.info("BatchHandlerT batchHandle success! ts:" + JsonUtil.toJson(ts));
        } catch (Exception e) {
            log.error("BatchHandlerT batchHandle failed!", e);
        }
    }

    public void handle(T t, int shardingKey) {
        if (null == t) return;
        int queueIndex = shardingKey % this.queueSize;
        AbstractBatchQueue<T> batchQueue = this.queueList.get(queueIndex);
        batchQueue.put(t);
        if (1 == threshHold || batchQueue.size() >= threshHold) {
            scheduledThreadPool.execute(this::batchHandle);
        }
    }

    public void handle(List<T> ts, int shardingKey) {
        if (Func.isEmpty(ts)) return;
        int queueIndex = shardingKey % this.queueSize;
        AbstractBatchQueue<T> batchQueue = this.queueList.get(queueIndex);
        batchQueue.put(ts);
        if (ts.size() >= threshHold || batchQueue.size() >= threshHold) {
            scheduledThreadPool.execute(this::batchHandle);
        }
    }

    public void suspend(List<T> ts, int shardingKey) {
        if (Func.isEmpty(ts)) return;
        int queueIndex = shardingKey % this.queueSize;
        AbstractBatchQueue<T> batchQueue = this.queueList.get(queueIndex);
        batchQueue.putFirst(ts);
    }
}
