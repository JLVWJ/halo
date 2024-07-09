package com.lvwj.halo.join;

import java.util.List;

/**
 *
 */
public interface JoinItemExecutorFactory {
     List<JoinItemExecutor> createForType(Class<?> cls);
}
