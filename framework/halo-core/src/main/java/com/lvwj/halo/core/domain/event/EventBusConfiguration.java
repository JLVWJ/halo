package com.lvwj.halo.core.domain.event;

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
        return new EventBus();
    }
}
