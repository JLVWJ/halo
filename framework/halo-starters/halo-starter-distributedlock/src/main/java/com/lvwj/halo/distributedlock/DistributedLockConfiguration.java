package com.lvwj.halo.distributedlock;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lvweijie
 * @date 2023年11月11日 16:43
 */
@AutoConfiguration
public class DistributedLockConfiguration {

    @Bean
    public DistributedLockFactory distributedLockFactory() {
        return new DistributedLockFactory();
    }

    @Bean
    public DistributedLockAspect distributedLockAspect(){
        return new DistributedLockAspect();
    }
}
