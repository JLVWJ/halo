package com.lvwh.halo.batchhandler.queue;

import cn.hutool.extra.spring.SpringUtil;
import com.lvwh.halo.batchhandler.BatchHandlerConstant;
import com.lvwj.halo.redis.RedisTemplatePlus;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lvweijie
 * @date 2023年12月21日 15:57
 */
public class RedisBatchQueue<T> extends AbstractBatchQueue<T> {

    private final String key;

    public RedisBatchQueue(String key) {
        this.key = key;
    }

    @Override
    public void put(T t) {
        if (null == t) {
            return;
        }
        redisPlus().rPush(cacheKey(), t);
    }

    @Override
    public void put(List<T> ts) {
        if (CollectionUtils.isEmpty(ts)) {
            return;
        }
        redisPlus().rPushAll(cacheKey(), ts);
    }

    @Override
    public void putFirst(List<T> ts) {
        if (CollectionUtils.isEmpty(ts)) {
            return;
        }
        redisPlus().lPushAll(cacheKey(), ts);
    }

    @Override
    public T take() {
        List<T> ts = take(1);
        return CollectionUtils.isEmpty(ts) ? null : ts.get(0);
    }

    @Override
    public List<T> take(int len) {
        if (len <= 0) {
            return Collections.emptyList();
        }
        String cacheKey = cacheKey();
        int lLen = redisPlus().lLen(cacheKey).intValue();
        int min = Math.min(lLen, len);
        if (min <= 0) {
            return Collections.emptyList();
        }
        return redisPlus().lPop(cacheKey, min);
    }

    @Override
    public List<T> takeAll() {
        return take(size());
    }

    @Override
    public int size() {
        return redisPlus().lLen(cacheKey()).intValue();
    }

    private static volatile RedisTemplatePlus redisPlus;

    private static RedisTemplatePlus redisPlus() {
        if (null == redisPlus) {
            synchronized (RedisBatchQueue.class) {
                if (null == redisPlus) {
                    redisPlus = SpringUtil.getBean(RedisTemplatePlus.class);
                }
            }
        }
        return redisPlus;
    }

    private String cacheKey() {
        return BatchHandlerConstant.CachePrefix + key;
    }

    private T waitToTake() {
        int i = 5;
        T o = null;
        while (i > 0) {
            try {
                o = redisPlus().blPop(cacheKey(), 1, TimeUnit.MINUTES);
                if (null != o) {
                    break;
                }
                i--;
            } catch (Exception e) {
                //time out
                i--;
            }
        }
        return o;
    }
}
