package com.lvwj.halo.redis.config;


import com.lvwj.halo.redis.idempotent.IdempotentAspect;
import com.lvwj.halo.redis.idempotent.key.DefaultIdempotentKey;
import com.lvwj.halo.redis.idempotent.key.ExpressionIdempotentKey;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
public class RedisIdempotentConfiguration {

  @Bean
  public IdempotentAspect idempotentAspect() {
    return new IdempotentAspect();
  }

  @Bean
  public DefaultIdempotentKey defaultIdempotentKey() {
    return new DefaultIdempotentKey();
  }

  @Bean
  public ExpressionIdempotentKey expressionIdempotentKey() {
    return new ExpressionIdempotentKey();
  }
}
