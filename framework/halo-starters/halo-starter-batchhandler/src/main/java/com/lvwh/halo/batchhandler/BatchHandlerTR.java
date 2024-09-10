package com.lvwh.halo.batchhandler;

import com.lvwh.halo.batchhandler.queue.AbstractBatchQueue;
import com.lvwh.halo.batchhandler.queue.MemoryBatchQueue;
import com.lvwj.halo.core.threadpool.ThreadPoolCache;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lvweijie
 * @date 2023年12月21日 16:27
 */
@Slf4j
public class BatchHandlerTR<T extends Serializable, R> {

    private final AbstractBatchQueue<BatchHandlerResultFuture<T, R>> queue;

    private final int threshHold;

    private final Function<List<T>, Map<T, R>> function;

    private final ThreadPoolTaskScheduler scheduledThreadPool;


    public BatchHandlerTR(String key, int threshHold, long interval, Function<List<T>, Map<T, R>> function) {
        threshHold = threshHold <= 1 ? BatchHandlerConstant.DEFAULT_THRESH_HOLD : threshHold;
        interval = interval <= 1 ? BatchHandlerConstant.DEFAULT_INTERVAL : interval;
        this.queue = new MemoryBatchQueue<>();
        this.threshHold = threshHold;
        this.function = function;
        this.scheduledThreadPool = ThreadPoolCache.getScheduledThreadPool(key);
        this.scheduledThreadPool.scheduleAtFixedRate(this::batchHandle, Duration.ofMillis(interval));
    }

    private void batchHandle() {
        try {
            List<BatchHandlerResultFuture<T, R>> ts = queue.take(threshHold);
            if (CollectionUtils.isEmpty(ts)) {
                return;
            }
            List<T> list = ts.stream().map(BatchHandlerResultFuture::getT).collect(Collectors.toList());
            Map<T, R> map = function.apply(list);
            ts.forEach(s -> s.getR().complete(map.get(s.getT())));
        } catch (Exception e) {
            log.error("[BatchHandlerTR] batchHandle failed!", e);
        }
    }

    public void handle(T t) {
        if (null == t) return;
        BatchHandlerResultFuture<T, R> future = new BatchHandlerResultFuture<>();
        future.setT(t);
        future.setR(new CompletableFuture<>());
        queue.put(future);
        if (queue.size() >= threshHold) {
            scheduledThreadPool.execute(this::batchHandle);
        }
    }

    @Data
    static class BatchHandlerResultFuture<T extends Serializable, R> implements Serializable {
        private T t;
        private CompletableFuture<R> r;
    }
}
