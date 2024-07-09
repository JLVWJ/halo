package com.lvwj.halo.redis.config;


import com.lvwj.halo.redis.RedisTemplatePlus;
import com.lvwj.halo.redis.serializer.RedisKeySerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * RedisTemplate配置
 *
 * @author lvwj
 */
@EnableCaching
@AutoConfiguration
public class RedisTemplateConfiguration {

  @Bean({"redisSerializer"})
  @ConditionalOnMissingBean(RedisSerializer.class)
  public RedisSerializer<Object> redisSerializer() {
    return new GenericJackson2JsonRedisSerializer();
  }

  @Bean({"redisTemplate"})
  @ConditionalOnMissingBean(name = {"redisTemplate"})
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, RedisSerializer<Object> redisSerializer) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    // key 序列化
    RedisKeySerializer keySerializer = new RedisKeySerializer();
    redisTemplate.setKeySerializer(keySerializer);
    redisTemplate.setHashKeySerializer(keySerializer);
    // value 序列化
    redisTemplate.setValueSerializer(redisSerializer);
    redisTemplate.setHashValueSerializer(redisSerializer);
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }

  @Bean({"redisTemplatePlus"})
  @ConditionalOnMissingBean(name = {"redisTemplatePlus"})
  public RedisTemplatePlus redisTemplatePlus(RedisTemplate<String, Object> redisTemplate) {
    return new RedisTemplatePlus(redisTemplate);
  }
}
