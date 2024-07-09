package com.lvwj.halo.join;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinInMemory {

    /**
     * 从 sourceData 中提取 key
     */
    String keyFromSourceData() default "id";

    /**
     * 从 joinData 中提取 key
     */
    String keyFromJoinData();

    /**
     * 批量加载 joinData
     */
    String joinDataLoader();

    /**
     * joinData 转换器
     */
    String joinDataConverter() default "";

    /**
     * 运行级别，同一级别的 join 可 并行执行
     */
    int runLevel() default 0;
}
