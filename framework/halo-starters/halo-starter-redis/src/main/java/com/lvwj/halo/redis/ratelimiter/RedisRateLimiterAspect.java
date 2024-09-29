package com.lvwj.halo.redis.ratelimiter;

import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.StringPool;
import com.lvwj.halo.core.spel.MyCachedExpressionEvaluator;
import com.lvwj.halo.redis.RedisTemplatePlus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * redis 限流
 *
 * @author lvweijie
 * @date 2022-12-14 16:18
 */
@Slf4j
@Aspect
public class RedisRateLimiterAspect implements ApplicationContextAware, EnvironmentAware {

  @Resource
  private RedisTemplatePlus redisTemplatePlus;

  private ApplicationContext applicationContext;

  private Environment environment;

  /**
   * AOP 环切 注解 @RateLimiter
   */
  @Around("@annotation(com.lvwj.halo.redis.ratelimiter.RateLimiter) || @annotation(com.lvwj.halo.redis.ratelimiter.RateLimiters)")
  public Object aroundRateLimiter(ProceedingJoinPoint point) throws Throwable {
    RateLimiter[] limiters;
    try {
      Method method = getMethod(point);
      limiters = method.getAnnotationsByType(RateLimiter.class);
    } catch (Throwable throwable) {
      return point.proceed();
    }

    for (RateLimiter limiter : limiters) {
      if (Func.toBoolean(resolve(limiter.enable()), Boolean.TRUE)) {
        checkRateLimit(point, limiter);
      }
    }

    return point.proceed();
  }

  private void checkRateLimit(ProceedingJoinPoint point, RateLimiter limiter) {
    String limitKey = limiter.value();
    Long max = Func.toLong(resolve(limiter.max()), 1L);
    Long ttl = Func.toLong(resolve(limiter.ttl()), 1L);
    TimeUnit timeUnit = TimeUnit.valueOf(Func.toStr(resolve(limiter.timeUnit()), "MINUTES"));
    Assert.hasText(limitKey, "@RateLimiter value must not be null or empty");
    Assert.isTrue(max > 0, "@RateLimiter[" + limitKey + "] max is invalid");
    Assert.isTrue(ttl > 0, "@RateLimiter[" + limitKey + "] ttl is invalid");
    Assert.notNull(timeUnit, "@RateLimiter[" + limitKey + "] timeUnit is invalid");

    String rateKey = limitKey;
    if (StringUtils.isNotBlank(limiter.param())) {
      String evalAsText = evalLimitParam(point, limiter.param());
      if (StringUtils.isNotBlank(evalAsText)) {
        rateKey = limitKey + StringPool.COLON + evalAsText;
      }
    }
    redisTemplatePlus.rateLimit(rateKey, max, ttl, timeUnit);
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

  private String resolve(String value) {
    if (Func.isNotBlank(value)) {
      return this.environment.resolvePlaceholders(value);
    }
    return value;
  }

  private Method getMethod(JoinPoint joinPoint) {
    Method method = null;
    try {
      Signature signature = joinPoint.getSignature();
      MethodSignature ms = (MethodSignature) signature;
      Object target = joinPoint.getTarget();
      method = target.getClass().getMethod(ms.getName(), ms.getParameterTypes());
    } catch (NoSuchMethodException e) {
      log.error("RedisRateLimiterAspect getMethod error", e);
    }
    return method;
  }

  @Override
  public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
}
