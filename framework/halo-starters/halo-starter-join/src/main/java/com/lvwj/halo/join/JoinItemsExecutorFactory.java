package com.lvwj.halo.join;

public interface JoinItemsExecutorFactory {
    /**
     * 为 类 创建 Join 执行器
     * @param cls
     */
     JoinItemsExecutor createFor(Class<?> cls);
}
