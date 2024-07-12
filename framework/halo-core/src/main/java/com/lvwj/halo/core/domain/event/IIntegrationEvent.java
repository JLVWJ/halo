package com.lvwj.halo.core.domain.event;

import java.time.LocalDateTime;

/**
 * 集成事件接口
 *
 * @author lvweijie
 * @date 2024年06月10日 12:59
 */
public interface IIntegrationEvent extends IEvent {

    /**
     * 集成事件ID
     */
    Long getEventId();

    /**
     * 集成事件生成时间
     */
    LocalDateTime getEventTime();

    /**
     * 集成事件tag
     */
    default String tag() {
        return this.getClass().getSimpleName().replace("Event", "").replace("Integration", "");
    }

    /**
     * 集成事件是否落库
     */
    default boolean isStore() {
        return true;
    }
}
