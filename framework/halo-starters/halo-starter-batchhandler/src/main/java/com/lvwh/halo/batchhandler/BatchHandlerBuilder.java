package com.lvwh.halo.batchhandler;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author lvweijie
 * @date 2023年12月21日 16:26
 */
public class BatchHandlerBuilder {
    private BatchHandlerBuilder() {

    }

    private static final Map<String, BatchHandlerT<?>> batchHandlerTMap = new ConcurrentHashMap<>();
    private static final Map<String, BatchHandlerTR<?, ?>> batchHandlerTRMap = new ConcurrentHashMap<>();

    public static <T extends Serializable> BatchHandlerT<T> buildMemory(String key, Consumer<List<T>> consumer) {
        return buildMemory(key, 1, 1, consumer);
    }

    public static <T extends Serializable, R> BatchHandlerTR<T, R> buildMemory(String key, Function<List<T>, Map<T, R>> function) {
        return buildMemory(key, 1, 1, function);
    }

    public static <T extends Serializable> BatchHandlerT<T> buildRedis(String key, Consumer<List<T>> consumer) {
        return buildRedis(key, 1, 1, consumer);
    }


    public static <T extends Serializable> BatchHandlerT<T> buildMemory(String key, int threshHold, long interval, Consumer<List<T>> consumer) {
        return (BatchHandlerT<T>) batchHandlerTMap.computeIfAbsent(key, k -> new BatchHandlerT<>(key, BatchHandlerType.MEMORY, threshHold, interval, consumer));
    }

    public static <T extends Serializable, R> BatchHandlerTR<T, R> buildMemory(String key, int threshHold, long interval, Function<List<T>, Map<T, R>> function) {
        return (BatchHandlerTR<T, R>) batchHandlerTRMap.computeIfAbsent(key, k -> new BatchHandlerTR<>(key, threshHold, interval, function));
    }

    public static <T extends Serializable> BatchHandlerT<T> buildRedis(String key, int threshHold, long interval, Consumer<List<T>> consumer) {
        return (BatchHandlerT<T>) batchHandlerTMap.computeIfAbsent(key, k -> new BatchHandlerT<>(key, BatchHandlerType.REDIS, threshHold, interval, consumer));
    }

    public static void remove(String key) {
        if (null == batchHandlerTMap.remove(key)) {
            batchHandlerTRMap.remove(key);
        }
    }
}
