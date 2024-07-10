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

    private Long id;
    private LocalDateTime created;

    public IntegrationEvent() {
    }

    @Override
    public Long getId() {
        if (null == this.id) {
            this.id = SnowflakeUtil.nextId();//雪花ID
        }
        return this.id;
    }

    @Override
    public LocalDateTime created() {
        if (null == created) {
            this.created = LocalDateTime.now();
        }
        return this.created;
    }
}
