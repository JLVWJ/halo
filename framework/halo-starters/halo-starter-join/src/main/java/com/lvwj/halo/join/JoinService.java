package com.lvwj.halo.join;

import java.util.Collections;
import java.util.List;

public interface JoinService {
    /**
     * 执行内存 join
     */
    default void joinInMemory(Object t) {
        if (t == null) {
            return;
        }
        joinInMemory(t.getClass(), Collections.singletonList(t));
    }

    default void joinInMemory(List<Object> t) {
        if (null == t || t.isEmpty()) {
            return;
        }
        if (t.size() == 1) {
            joinInMemory(t.get(0));
        } else {
            joinInMemory(t.get(0).getClass(), t);
        }
    }

    /**
     * 执行内存 Join
     *
     * @param tCls 实际类型
     * @param t    需要抓取的集合
     */
    void joinInMemory(Class<?> tCls, List<Object> t);

    /**
     * 注册一个类型，主要用于初始化
     *
     * @param tCls
     */
    void register(Class<?> tCls);
}
