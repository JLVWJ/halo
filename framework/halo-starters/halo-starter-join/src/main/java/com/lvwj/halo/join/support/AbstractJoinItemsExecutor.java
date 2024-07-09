package com.lvwj.halo.join.support;

import com.google.common.base.Preconditions;
import com.lvwj.halo.join.JoinItemExecutor;
import com.lvwj.halo.join.JoinItemsExecutor;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

public abstract class AbstractJoinItemsExecutor implements JoinItemsExecutor {

    @Getter(AccessLevel.PROTECTED)
    private final Class<?> dataCls;

    @Getter(AccessLevel.PROTECTED)
    private final List<JoinItemExecutor> joinItemExecutors;

    public AbstractJoinItemsExecutor(Class<?> dataCls, List<JoinItemExecutor> joinItemExecutors) {
        Preconditions.checkArgument(dataCls != null);
        Preconditions.checkArgument(joinItemExecutors != null);

        this.dataCls = dataCls;
        this.joinItemExecutors = joinItemExecutors;
        this.joinItemExecutors.sort(Comparator.comparingInt(JoinItemExecutor::runOnLevel));
    }
}
