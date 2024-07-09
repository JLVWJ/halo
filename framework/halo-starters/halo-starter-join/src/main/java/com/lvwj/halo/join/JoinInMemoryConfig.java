package com.lvwj.halo.join;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinInMemoryConfig {
    JoinInMemoryExecutorType executorType() default JoinInMemoryExecutorType.SERIAL;
    String executorName() default "defaultExecutor";
}
