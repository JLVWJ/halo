package com.lvwj.halo.join.support;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.lvwj.halo.common.utils.CollectionUtil;
import com.lvwj.halo.core.node.ForestNodeMerger;
import com.lvwj.halo.join.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
public class DefaultJoinItemsExecutorFactory implements JoinItemsExecutorFactory {
    private final List<JoinItemExecutorFactory> joinItemExecutorFactories;
    private final Map<String, ExecutorService> executorServiceMap;

    public DefaultJoinItemsExecutorFactory(Collection<? extends JoinItemExecutorFactory> joinItemExecutorFactories,
                                           Map<String, ExecutorService> executorServiceMap) {
        this.joinItemExecutorFactories = Lists.newArrayList(joinItemExecutorFactories);

        // 按执行顺序进行排序
        AnnotationAwareOrderComparator.sort(this.joinItemExecutorFactories);
        this.executorServiceMap = executorServiceMap;
    }

    @Override
    public JoinItemsExecutor createFor(Class<?> cls) {
        // 依次遍历 JoinItemExecutorFactory， 收集 JoinItemExecutor 信息
        List<JoinItemExecutor> joinItemExecutors = this.joinItemExecutorFactories.stream()
                .flatMap(factory -> factory.createForType(cls).stream())
                .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(joinItemExecutors)) return null;

        // 封装为 JoinItemsExecutor
        return buildJoinItemsExecutor(cls, joinItemExecutors);
    }

    private JoinItemsExecutor buildJoinItemsExecutor(Class<?> cls, List<JoinItemExecutor> joinItemExecutors) {
        //按level排序
        joinItemExecutors.sort(Comparator.comparingInt(JoinItemExecutor::runOnLevel));
        //集合构造成树结构
        List<JoinItemExecutor> merge = ForestNodeMerger.merge(joinItemExecutors);

        // 从 class 上读取配置信息
        JoinInMemoryConfig joinInMemoryConfig = cls.getAnnotation(JoinInMemoryConfig.class);

        // 使用 串行执行器
        if (joinInMemoryConfig == null || joinInMemoryConfig.executorType() == JoinInMemoryExecutorType.SERIAL) {
            log.info("JoinInMemory for {} use serial executor", cls);
            return new SerialJoinItemsExecutor(cls, merge);
        }

        // 使用 并行执行器
        if (joinInMemoryConfig.executorType() == JoinInMemoryExecutorType.PARALLEL) {
            log.info("JoinInMemory for {} use parallel executor, pool is {}", cls, joinInMemoryConfig.executorName());
            ExecutorService executor = executorServiceMap.get(joinInMemoryConfig.executorName());
            Preconditions.checkArgument(executor != null);
            return new ParallelJoinItemsExecutor(cls, merge, executor);
        }
        throw new IllegalArgumentException();
    }
}
