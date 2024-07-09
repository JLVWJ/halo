package com.lvwh.halo.batchhandler;

/**
 * @author lvweijie
 * @date 2023年12月21日 18:25
 */
public class BatchHandlerConstant {

    private BatchHandlerConstant(){}

    public static final long DEFAULT_INTERVAL = 50;
    public static final int DEFAULT_THRESH_HOLD = 50;

    public static final int DEFAULT_INITIAL_DELAY = 10;

    public static final String CachePrefix = "BatchQueue:";
}
