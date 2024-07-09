package com.lvwj.halo.statemachine;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lvweijie
 * @date 2024年07月03日 13:15
 */
@AutoConfiguration
public class StateMachineConfiguration {

    @Bean
    public StateMachineRegistry stateMachineRegistry() {
        return new StateMachineRegistry();
    }
}
