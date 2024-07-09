package com.lvwj.halo.core.domain.event;

/**
 * 事件存储接口
 *
 * @author lvweijie
 * @date 2023年11月20日 15:43
 */
public interface IEventStorage {

    <E extends IEvent> void save(E event);
}
