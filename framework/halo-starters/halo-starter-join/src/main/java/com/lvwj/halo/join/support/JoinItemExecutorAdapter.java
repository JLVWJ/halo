package com.lvwj.halo.join.support;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by taoli on 2022/7/31.
 */
@Slf4j
@Builder
@Getter
public class JoinItemExecutorAdapter extends AbstractJoinItemExecutor {
    private final String name;
    private final Class<?> clazz;
    private final Field field;
    private final int runLevel;

    private final Function<Object, Object> keyFromSourceData;
    private final Function<List<Object>, List<Object>> joinDataLoader;
    private final Function<Object, Object> keyFromJoinData;
    private final Function<Object, Object> joinDataConverter;
    private final BiConsumer<Object, List<Object>> foundCallback;
    private final BiConsumer<Object, Object> lostCallback;


    public JoinItemExecutorAdapter(String name,
                                   Class<?> clazz,
                                   Field field,
                                   Integer runLevel,
                                   Function<Object, Object> keyFromSourceData,
                                   Function<List<Object>, List<Object>> joinDataLoader,
                                   Function<Object, Object> keyFromJoinData,
                                   Function<Object, Object> joinDataConverter,
                                   BiConsumer<Object, List<Object>> foundCallback,
                                   BiConsumer<Object, Object> lostCallback) {
        Preconditions.checkArgument(field != null);
        Preconditions.checkArgument(clazz != null);
        Preconditions.checkArgument(keyFromSourceData != null);
        Preconditions.checkArgument(joinDataLoader != null);
        Preconditions.checkArgument(keyFromJoinData != null);
        Preconditions.checkArgument(joinDataConverter != null);
        Preconditions.checkArgument(foundCallback != null);

        this.name = name;
        this.clazz = clazz;
        this.field = field;
        this.keyFromSourceData = keyFromSourceData;
        this.joinDataLoader = joinDataLoader;
        this.keyFromJoinData = keyFromJoinData;
        this.joinDataConverter = joinDataConverter;
        this.foundCallback = foundCallback;

        if (lostCallback != null) {
            this.lostCallback = getDefaultLostFunction().andThen(lostCallback);
        } else {
            this.lostCallback = getDefaultLostFunction();
        }

        if (runLevel == null) {
            this.runLevel = 0;
        } else {
            this.runLevel = runLevel;
        }
    }

    @Override
    protected Object createJoinKeyFromSourceData(Object data) {
        return this.keyFromSourceData.apply(data);
    }

    @Override
    protected List<Object> getJoinDataByJoinKeys(List<Object> joinKeys) {
        return this.joinDataLoader.apply(joinKeys);
    }

    @Override
    protected Object createJoinKeyFromJoinData(Object joinData) {
        return this.keyFromJoinData.apply(joinData);
    }

    @Override
    protected Object convertToResult(Object joinData) {
        return this.joinDataConverter.apply(joinData);
    }

    @Override
    protected void onFound(Object data, List<Object> JoinResults) {
        this.foundCallback.accept(data, JoinResults);
    }

    @Override
    protected void onNotFound(Object data, Object joinKey) {
        this.lostCallback.accept(data, joinKey);
    }

    private BiConsumer<Object, Object> getDefaultLostFunction() {
        return (data, joinKey) -> log.warn("failed to find join data by {} for {}", joinKey, data);
    }

    @Override
    public int runOnLevel() {
        return this.runLevel;
    }

    @Override
    public String toString() {
        return "JoinExecutorAdapter-for-" + name;
    }
}
