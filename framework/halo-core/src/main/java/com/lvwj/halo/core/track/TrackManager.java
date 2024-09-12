package com.lvwj.halo.core.track;

import com.lvwj.halo.common.models.entity.IEntity;
import org.javers.core.Changes;
import org.javers.core.ChangesByObject;
import org.springframework.util.CollectionUtils;

import java.util.List;

public interface TrackManager<E extends IEntity<?>> {

    /**
     * 开启变更追踪
     *
     * @param e 聚合根对应的数据实体
     * @author lvweijie
     * @date 2023/11/3 15:39
     */
    void attach(E e);

    default void attach(List<E> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(this::attach);
    }

    /**
     * 合并修改过的数据实体到本地变量
     *
     * @param e 聚合根对应的数据实体
     * @author lvweijie
     * @date 2023/11/6 20:03
     */
    default void merge(E e) {
        this.attach(e);
    }

    default void merge(List<E> list) {
        attach(list);
    }

    /**
     * 解除变更追踪
     *
     * @param e 聚合根对应的数据实体
     * @author lvweijie
     * @date 2023/11/3 15:39
     */
    void detach(E e);

    default void detach(List<E> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(this::detach);
    }

    /**
     * 获取变更差异数据
     *
     * @param e 聚合根对应的数据实体
     * @return org.javers.core.Changes
     * @author lvweijie
     * @date 2023/11/3 19:21
     */
    Changes changeDiff(E e);

    Changes changeDiff(List<E> list, Class<E> clazz);

    List<ChangesByObject> changeDiffByObject(E e);

    List<ChangesByObject> changeDiffByObject(List<E> list, Class<E> clazz);

    /**
     * 获取删除差异数据
     *
     * @param e 聚合根对应的数据实体
     * @return org.javers.core.Changes
     * @author lvweijie
     * @date 2023/11/3 19:21
     */
    Changes deleteDiff(E e);

    Changes deleteDiff(List<E> list, Class<E> clazz);

    List<ChangesByObject> deleteDiffByObject(E e);

    List<ChangesByObject> deleteDiffByObject(List<E> list, Class<E> clazz);
}
