package com.lvwj.halo.redis.ratelimiter;

import com.lvwj.halo.core.spel.MyCachedExpressionEvaluator;
import com.lvwj.halo.redis.RedisTemplatePlus;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * redis 限流
 *
 * @author lvweijie
 * @date 2022-12-14 16:18
 */
@Aspect
@RequiredArgsConstructor
public class RedisRateLimiterAspect implements ApplicationContextAware {

  @Resource
  private RedisTemplatePlus redisTemplatePlus;

  private ApplicationContext applicationContext;

  /**
   * AOP 环切 注解 @RateLimiter
   */
  @Around("@annotation(limiter)")
  public Object aroundRateLimiter(ProceedingJoinPoint point, RateLimiter limiter) {
    String limitKey = limiter.value();
    Assert.hasText(limitKey, "@RateLimiter value must not be null or empty");
    String rateKey = limitKey;
    if (StringUtils.isNotBlank(limiter.param())) {
      String evalAsText = evalLimitParam(point, limiter.param());
      if (StringUtils.isNotBlank(evalAsText)) {
        rateKey = limitKey + ":" + evalAsText;
      }
    }
    return redisTemplatePlus.rateLimit(rateKey, limiter.max(), limiter.ttl(), limiter.timeUnit(), () -> point.proceed());
  }

  /**
   * 计算参数表达式
   *
   * @param point      ProceedingJoinPoint
   * @param limitParam limitParam
   * @return 结果
   */
  private String evalLimitParam(ProceedingJoinPoint point, String limitParam) {
    MethodSignature ms = (MethodSignature) point.getSignature();
    Method method = ms.getMethod();
    Object[] args = point.getArgs();
    Object target = point.getTarget();
    Class<?> targetClass = target.getClass();
    EvaluationContext context = MyCachedExpressionEvaluator.INSTANCE.createContext(method, args, target, targetClass, applicationContext);
    AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
    return MyCachedExpressionEvaluator.INSTANCE.evalAsText(limitParam, elementKey, context);
  }

  @Override
  public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
