package com.lvwj.halo.join.support;

import com.lvwj.halo.common.utils.CollectionUtil;
import com.lvwj.halo.join.JoinItemExecutor;
import com.lvwj.halo.join.JoinItemExecutorFactory;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * 抽象类
 */
abstract class AbstractJoinItemExecutorFactory<A extends Annotation> implements JoinItemExecutorFactory {
    private final Class<A> annCls;

    protected AbstractJoinItemExecutorFactory(Class<A> annCls) {
        this.annCls = annCls;
    }

    @Override
    public List<JoinItemExecutor> createForType(Class<?> cls) {
        List<JoinItemExecutor> executors = new ArrayList<>();
        doCreateForType(cls, executors);
        return executors;
    }

    private void doCreateForType(Class<?> cls, List<JoinItemExecutor> executors) {
        List<JoinItemExecutor> executorList = FieldUtils.getAllFieldsList(cls).stream()
                .map(field -> createForField(cls, field, AnnotatedElementUtils.findMergedAnnotation(field, annCls)))
                .filter(Objects::nonNull)
                .collect(toList());
        if (CollectionUtil.isNotEmpty(executorList)) {
            executors.addAll(executorList);
            for (JoinItemExecutor executor : executorList) {
                doCreateForType(executor.getClazz(), executors);
            }
        }
    }

    private JoinItemExecutor createForField(Class<?> cls, Field field, A ann) {
        Class<?> fieldType = fieldActualType(field);
        if (ann == null || ClassUtils.isPrimitiveOrWrapper(fieldType) || fieldType.isEnum() || fieldType.isArray() || Map.class.isAssignableFrom(fieldType)) {
            return null;
        }
        JoinItemExecutorAdapter adapter = JoinItemExecutorAdapter.builder()
                .field(field)
                .clazz(fieldType)
                .name(createName(cls, field, ann))
                .runLevel(createRunLevel(cls, field, ann))
                .keyFromSourceData(createKeyFromSourceData(cls, field, ann))
                .joinDataLoader(createJoinDataLoader(cls, field, ann))
                .keyFromJoinData(createKeyFromJoinData(cls, field, ann))
                .joinDataConverter(createJoinDataConverter(cls, field, ann))
                .foundCallback(createFoundFunction(cls, field, ann))
                .lostCallback(createLostFunction(cls, field, ann))
                .build();
        adapter.setParentId(cls.getSimpleName());
        return adapter;
    }

    private Class<?> fieldActualType(Field field) {
        Class<?> fieldActualType = field.getType();
        if (Collection.class.isAssignableFrom(fieldActualType)) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                //得到泛型里的class类型对象
                fieldActualType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
            }
        }
        return fieldActualType;
    }


    protected abstract Function<Object, Object> createKeyFromSourceData(Class<?> cls, Field field, A ann);

    protected abstract Function<Object, Object> createKeyFromJoinData(Class<?> cls, Field field, A ann);

    protected abstract Function<List<Object>, List<Object>> createJoinDataLoader(Class<?> cls, Field field, A ann);

    protected abstract Function<Object, Object> createJoinDataConverter(Class<?> cls, Field field, A ann);

    protected abstract BiConsumer<Object, List<Object>> createFoundFunction(Class<?> cls, Field field, A ann);

    protected BiConsumer<Object, Object> createLostFunction(Class<?> cls, Field field, A ann) {
        return null;
    }


    protected abstract int createRunLevel(Class<?> cls, Field field, A ann);

    protected String createName(Class<?> cls, Field field, A ann) {
        return "class[" + cls.getSimpleName() + "]" +
                "#field[" + field.getName() + "]" +
                "-" + ann.getClass().getSimpleName();
    }
}
