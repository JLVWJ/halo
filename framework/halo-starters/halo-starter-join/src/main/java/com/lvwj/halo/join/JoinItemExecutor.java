package com.lvwj.halo.join;

import com.lvwj.halo.core.node.INode;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by taoli on 2022/7/31.
 */
public interface JoinItemExecutor extends INode<JoinItemExecutor> {
    void execute(List<Object> datas);

    default int runOnLevel() {
        return 0;
    }

    Field getField();

    Class<?> getClazz();

    @Override
    default String getId() {
        return getClazz().getSimpleName();
    }
}
