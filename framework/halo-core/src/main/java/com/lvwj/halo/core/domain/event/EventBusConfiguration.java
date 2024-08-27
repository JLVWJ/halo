package com.lvwj.halo.core.domain.event;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author lvweijie
 * @date 2024年07月09日 14:45
 */
@AutoConfiguration
public class EventBusConfiguration {

    @Bean
    public IEventBus eventBus() {
        EventBus eventBus = new EventBus();
        eventBus.register(EventBus.GLOBAL_SUBSCRIBER, SpringUtil::publishEvent);
        return eventBus;
    }
}
