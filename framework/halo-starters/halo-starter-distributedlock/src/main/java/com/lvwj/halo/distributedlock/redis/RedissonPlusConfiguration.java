package com.lvwj.halo.distributedlock.redis;

import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lvweijie
 * @date 2023年11月11日 16:43
 */
@AutoConfiguration
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonPlusConfiguration {

    @Bean
    public RedissonClientPlus redissonClientPlus(RedisProperties redisProperties) {
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        String password = redisProperties.getPassword();
        int database = redisProperties.getDatabase();
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password)
                .setDatabase(database);
        return RedissonPlus.create(config);
    }
}
