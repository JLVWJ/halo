package com.lvwh.halo.batchhandler;

import com.lvwh.halo.batchhandler.queue.AbstractBatchQueue;
import com.lvwh.halo.batchhandler.queue.MemoryBatchQueue;
import com.lvwh.halo.batchhandler.queue.RedisBatchQueue;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.core.threadpool.ThreadPoolCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author lvweijie
 * @date 2023年12月21日 16:27
 */
@Slf4j
public class BatchHandlerT<T extends Serializable> {

    private final AbstractBatchQueue<T> queue;

    private final int threshHold;

    private final Consumer<List<T>> consumer;

    private final ThreadPoolTaskScheduler scheduledThreadPool;


    public BatchHandlerT(String key, BatchHandlerType type, int threshHold, long interval, Consumer<List<T>> consumer) {
        threshHold = threshHold <= 1 ? BatchHandlerConstant.DEFAULT_THRESH_HOLD : threshHold;
        interval = interval <= 1 ? BatchHandlerConstant.DEFAULT_INTERVAL : interval;
        this.queue = BatchHandlerType.REDIS.equals(type) ? new RedisBatchQueue<>(key) : new MemoryBatchQueue<>();
        this.threshHold = threshHold;
        this.consumer = consumer;
        this.scheduledThreadPool = ThreadPoolCache.getScheduledThreadPool(key);
        this.scheduledThreadPool.scheduleAtFixedRate(this::batchHandle, Duration.ofMillis(interval));
    }

    private void batchHandle() {
        try {
            List<T> ts = queue.takeAll();
            if (CollectionUtils.isEmpty(ts)) {
                return;
            }
            consumer.accept(ts);
            //log.info("BatchHandlerT batchHandle success! ts:" + JsonUtil.toJson(ts));
        } catch (Exception e) {
            log.error("BatchHandlerT batchHandle failed!", e);
        }
    }

    public void handle(T t) {
        if (null == t) return;
        queue.put(t);
        if (1 == threshHold || queue.size() >= threshHold) {
            scheduledThreadPool.execute(this::batchHandle);
        }
    }

    public void handle(List<T> ts) {
        if (Func.isEmpty(ts)) return;
        queue.put(ts);
        if (ts.size() >= threshHold || queue.size() >= threshHold) {
            scheduledThreadPool.execute(this::batchHandle);
        }
    }

    public void suspend(List<T> ts) {
        if (Func.isEmpty(ts)) return;
        queue.putFirst(ts);
    }
}
