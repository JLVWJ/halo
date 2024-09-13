package com.lvwj.halo.mybatisplus.service.impl;

import com.lvwj.halo.common.models.entity.IEntity;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.ObjectUtil;
import com.lvwj.halo.common.utils.TransactionUtil;
import com.lvwj.halo.core.track.TrackManager;
import com.lvwj.halo.core.track.impl.ThreadLocalTrackManager;
import com.lvwj.halo.mybatisplus.entity.EntityHolder;
import com.lvwj.halo.mybatisplus.mapper.CustomMapper;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Changes;
import org.javers.core.ChangesByObject;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.*;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

/**
 * 追踪服务实现
 * 原理：基于ThreadLocal存储快照，基于Javers对比差异数据
 *
 * @author lvweijie
 * @date 2023年11月03日 16:40
 */
@Slf4j
public abstract class TrackServiceImpl<M extends CustomMapper<T>, T extends IEntity<?>> extends JoinServiceImpl<M,T> {

    protected final TrackManager<T> trackManager;

    public TrackServiceImpl() {
        this.trackManager = new ThreadLocalTrackManager<>();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveByTrack(T t) {
        if (null == t) {
            return;
        }
        boolean hasVersionField = EntityHolder.hasVersionField(getEntityClass());
        if (hasVersionField) {
            Object verObj = EntityHolder.getFieldValue(t, EntityHolder.VERSION);
            Long version = verObj != null ? Long.parseLong(verObj.toString()) + 1 : 0;
            EntityHolder.setFieldValue(t, EntityHolder.VERSION, version);
        }
        Changes changes = this.trackManager.changeDiff(t);
        doSave(changes);
        //this.trackManager.merge(t); 更新快照数据，为了可以多次saveByTrack
        //事务完成后，解除变更追踪
        TransactionUtil.afterCompletion(() -> this.trackManager.detach(t));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveByTrack(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        boolean hasVersionField = EntityHolder.hasVersionField(getEntityClass());
        if (hasVersionField) {
            for (T t : list) {
                Object verObj = EntityHolder.getFieldValue(t, EntityHolder.VERSION);
                Long version = verObj != null ? Long.parseLong(verObj.toString()) + 1 : 0;
                EntityHolder.setFieldValue(t, EntityHolder.VERSION, version);
            }
        }
        Changes changes = this.trackManager.changeDiff(list, getEntityClass());
        doSave(changes);
        //this.trackManager.merge(list); 更新快照数据，为了可以多次saveByTrack
        //事务完成后，解除变更追踪
        TransactionUtil.afterCompletion(() -> this.trackManager.detach(list));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeByTrack(T t) {
        if (null == t) {
            return;
        }
        if (entityClassIsJoin()) {
            Changes changes = this.trackManager.deleteDiff(t);
            doDelete(changes);
        } else {
            super.removeById(t);
        }
        TransactionUtil.afterCompletion(() -> this.trackManager.detach(t));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeByTrack(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        if (entityClassIsJoin()) {
            Changes changes = this.trackManager.deleteDiff(list, getEntityClass());
            doDelete(changes);
        } else {
            super.removeBatchByIds(list);
        }
        //事务完成后，解除变更追踪
        TransactionUtil.afterCompletion(() -> this.trackManager.detach(list));
    }

    @Override
    public TrackManager<T> getTrackManager() {
        return this.trackManager;
    }

    private void doSave(List<ChangesByObject> changes) {
        if (Func.isEmpty(changes)) {
            return;
        }



    }

    private void doSave(Changes changes) {
        if (null == changes || changes.isEmpty()) {
            return;
        }
        Map<String, List<Change>> createMap = new HashMap<>();
        Map<String, List<Change>> deleteMap = new HashMap<>();
        Map<String, Map<Object, List<Change>>> updateMap = new HashMap<>();
        for (Change change : changes) {
            String typeName = change.getAffectedGlobalId().getTypeName();
            if (isCreate(change)) {
                List<Change> list = createMap.computeIfAbsent(typeName, k -> new ArrayList<>());
                list.add(change);
            } else if (isDelete(change)) {
                List<Change> list = deleteMap.computeIfAbsent(typeName, k -> new ArrayList<>());
                list.add(change);
            }else if (isUpdate(change)) {
                Map<Object, List<Change>> map = updateMap.computeIfAbsent(typeName, k -> new HashMap<>());
                List<Change> list = map.computeIfAbsent(getEntityId(change), k -> new ArrayList<>());
                list.add(change);
            }
        }
        doCreate(createMap);
        doUpdate(updateMap);
        doDelete(deleteMap);
    }

    private void doCreate(Map<String, List<Change>> map) {
        if (map.isEmpty()) {
            return;
        }
        List<Object> entities = new ArrayList<>();
        for (Map.Entry<String, List<Change>> entry : map.entrySet()) {
            for (Change change : entry.getValue()) {
                change.getAffectedObject().ifPresent(entities::add);
            }
            if (!CollectionUtils.isEmpty(entities)) {
                if (entities.size() == 1) {
                    EntityHolder.getMapper(entry.getKey()).insert(entities.get(0));
                } else {
                    EntityHolder.getMapper(entry.getKey()).insertBatchSomeColumn(entities);
                }
                entities.clear();
            }
        }
    }

    private void doDelete(Map<String, List<Change>> map) {
        for (Map.Entry<String, List<Change>> entry : map.entrySet()) {
            Set<Object> entityIds = entry.getValue().stream().map(this::getEntityId).collect(toSet());
            if (!CollectionUtils.isEmpty(entityIds)) {
                CustomMapper mapper = EntityHolder.getMapper(entry.getKey());
                mapper.deleteByIds(entityIds);
            }
        }
    }

    private void doUpdate(Map<String, Map<Object, List<Change>>> map) {
        if (map.isEmpty()) {
            return;
        }
        List<Object> entities = new ArrayList<>();
        for (Map.Entry<String, Map<Object, List<Change>>> entityEntry : map.entrySet()) {
            Class<?> entityClass = EntityHolder.getEntityClass(entityEntry.getKey());
            for (Map.Entry<Object, List<Change>> itemEntry : entityEntry.getValue().entrySet()) {
                if (null == itemEntry.getKey()) {
                    continue;
                }
                Object entity = null;
                for (Change change : itemEntry.getValue()) {
                    PropertyChange<Object> valueChange = (PropertyChange<Object>) change;
                    EntityHolder.EntityField entityField = EntityHolder.getEntityField(entityClass, valueChange.getPropertyName());
                    if (entityField.allowUpdate(valueChange.getRight())) {
                        if (entity == null) {
                            entity = BeanUtils.instantiateClass(entityClass);
                            EntityHolder.setFieldValue(entity, "id", itemEntry.getKey());
                        }
                        EntityHolder.setFieldValue(entity, valueChange.getPropertyName(), valueChange.getRight());
                    }
                }
                if (null != entity) {
                    entities.add(entity);
                }
            }
            if (!CollectionUtils.isEmpty(entities)) {
                EntityHolder.getMapper(entityClass).updateBatch(entities);
                entities.clear();
            }
        }
    }

    private void doDelete(Changes changes) {
        if (null == changes || changes.isEmpty()) {
            return;
        }
        Map<String, List<Change>> map = changes.stream().filter(this::isDelete).collect(groupingBy(s -> s.getAffectedGlobalId().getTypeName()));
        doDelete(map);
    }

    private boolean isCreate(Change change) {
        return change instanceof NewObject;
    }

    private boolean isUpdate(Change change) {
        return change instanceof ValueChange && !(change instanceof InitialValueChange) && !(change instanceof TerminalValueChange);
    }

    private boolean isDelete(Change change) {
        return change instanceof ObjectRemoved;
    }

    private boolean existsInMap(Map<String, List<Change>> map, Change change) {
        if (Func.isEmpty(map) || null == change) return false;
        String typeName = change.getAffectedGlobalId().getTypeName();
        Object entityId = getEntityId(change);
        List<Change> list = map.get(typeName);
        return Func.isNotEmpty(list) && list.stream().anyMatch(s -> ObjectUtil.nullSafeEquals(getEntityId(s), entityId));
    }

    /**
     * 获取唯一标识id
     *
     * @author lvwj
     * @date 2022-12-08 18:06
     */
    private Object getEntityId(Change change) {
        if (change.getAffectedGlobalId() instanceof InstanceId instanceId) {
            //带@id的对象属性变动
            return instanceId.getCdoId();
        } else {
            //其他全部当做ValueObject对象处理
            return ((InstanceId) ((ValueObjectId) change.getAffectedGlobalId()).getOwnerId()).getCdoId();
        }
    }
}
