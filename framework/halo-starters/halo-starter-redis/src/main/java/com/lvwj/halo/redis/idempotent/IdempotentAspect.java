package com.lvwj.halo.redis.idempotent;

import cn.hutool.extra.spring.SpringUtil;
import com.lvwj.halo.redis.RedisTemplatePlus;
import com.lvwj.halo.redis.idempotent.key.IdempotentKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.Assert;


/**
 * 拦截声明了 {@link Idempotent} 注解的方法，实现幂等操作
 *
 * @author lvwj
 * @date 2022-08-17 19:36
 */
@Aspect
@Slf4j
public class IdempotentAspect {

  @Resource
  private RedisTemplatePlus redisTemplatePlus;

  @Around("@annotation(idempotent)")
  public Object aroundIdempotent(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
    IdempotentKey keyResolver = SpringUtil.getBean(idempotent.keyResolver());
    Assert.notNull(keyResolver, "找不到对应的IdempotentKey");
    // 解析 Key
    String key = keyResolver.resolver(joinPoint, idempotent);
    Assert.hasText(key, "IdempotentKey resolver result is blank!");
    return redisTemplatePlus.idempotent(key, idempotent.timeout(), idempotent.timeUnit(), () -> joinPoint.proceed());
  }
}
