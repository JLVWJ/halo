package com.lvwj.halo.cache.config;

import com.lvwj.halo.cache.core.constant.CacheConstant;
import com.lvwj.halo.cache.core.manager.multi.HaloMultiLevelCacheManager;
import com.lvwj.halo.cache.core.message.CacheMessageListener;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Objects;

@AutoConfiguration(after = {HaloCacheConfigration.class})
@ConditionalOnProperty(prefix = HaloCacheProperties.PREFIX, value = "enabled", havingValue = "true")
public class HaloCacheMessageListenerConfigration {

    @Resource
    private RedisSerializer<Object> redisSerializer;

    @Bean
    @ConditionalOnClass(HaloMultiLevelCacheManager.class)
    @ConditionalOnProperty(prefix = HaloCacheProperties.PREFIX, value = "type", havingValue = "multilevel")
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                       HaloMultiLevelCacheManager multilevelCacheManager) {

        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(Objects.requireNonNull(redisConnectionFactory));
        CacheMessageListener cacheMessageListener = new CacheMessageListener(redisSerializer, multilevelCacheManager);
        redisMessageListenerContainer.addMessageListener(cacheMessageListener, new ChannelTopic(CacheConstant.CLEAR_LOCAL_TOPIC));
        return redisMessageListenerContainer;
    }
}
