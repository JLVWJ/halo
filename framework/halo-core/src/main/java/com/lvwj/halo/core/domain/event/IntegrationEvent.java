package com.lvwj.halo.core.domain.event;

import com.lvwj.halo.core.snowflake.SnowflakeUtil;

import java.time.LocalDateTime;

/**
 * 集成事件：用于上下文之间跨服务跨进程通信，可作为MQ消息体基类使用。
 *
 * @author lvweijie
 * @date 2024年06月10日 13:05
 */
public abstract class IntegrationEvent implements IIntegrationEvent {

    private final Long id;
    private final LocalDateTime created;

    public IntegrationEvent() {
        this.id = SnowflakeUtil.nextId();//雪花ID
        this.created = LocalDateTime.now();
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public LocalDateTime created() {
        return this.created;
    }
}
