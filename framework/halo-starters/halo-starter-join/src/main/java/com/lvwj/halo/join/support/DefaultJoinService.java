package com.lvwj.halo.join.support;

import com.google.common.collect.Maps;
import com.lvwj.halo.join.JoinItemsExecutor;
import com.lvwj.halo.join.JoinItemsExecutorFactory;
import com.lvwj.halo.join.JoinService;

import java.util.List;
import java.util.Map;

/**
 * 关联服务
 */
public class DefaultJoinService implements JoinService {
    private final JoinItemsExecutorFactory joinItemsExecutorFactory;
    private final Map<Class<?>, JoinItemsExecutor> cache = Maps.newConcurrentMap();

    public DefaultJoinService(JoinItemsExecutorFactory joinItemsExecutorFactory) {
        this.joinItemsExecutorFactory = joinItemsExecutorFactory;
    }

    @Override
    public void joinInMemory(Class<?> tCls, List<Object> t) {
        JoinItemsExecutor executor = this.cache.computeIfAbsent(tCls, this::createJoinExecutorGroup);
        if (null != executor) {
            executor.execute(t);
        }
    }

    @Override
    public void register(Class<?> tCls) {
        this.cache.computeIfAbsent(tCls, this::createJoinExecutorGroup);
    }

    private JoinItemsExecutor createJoinExecutorGroup(Class<?> aClass) {
        return this.joinItemsExecutorFactory.createFor(aClass);
    }
}
