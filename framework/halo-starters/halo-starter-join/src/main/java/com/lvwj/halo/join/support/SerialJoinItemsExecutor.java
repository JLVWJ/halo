package com.lvwj.halo.join.support;

import com.lvwj.halo.common.utils.CollectionUtil;
import com.lvwj.halo.join.JoinItemExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class SerialJoinItemsExecutor extends AbstractJoinItemsExecutor {
    public SerialJoinItemsExecutor(Class<?> dataCls, List<JoinItemExecutor> joinItemExecutors) {
        super(dataCls, joinItemExecutors);
    }

    @Override
    public void execute(List<Object> datas) {
        doExecute(getJoinItemExecutors(), datas);
    }

    private void doExecute(List<JoinItemExecutor> executors, List<Object> datas) {
        if (CollectionUtil.isEmpty(executors) || CollectionUtil.isEmpty(datas)) return;
        for (JoinItemExecutor executor : executors) {
            executor.execute(datas);
            if (executor.getHasChildren()) {
                List<Object> list = datas.stream().map(s -> getFieldValue(executor.getField(), s))
                        .filter(Objects::nonNull).collect(Collectors.toList());
                doExecute(executor.getChildren(), list);
            }
        }
    }

    private Object getFieldValue(Field field, Object obj) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        return ReflectionUtils.getField(field, obj);
    }
}
