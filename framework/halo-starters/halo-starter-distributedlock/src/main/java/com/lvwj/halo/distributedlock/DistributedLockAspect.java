package com.lvwj.halo.distributedlock;

import com.lvwj.halo.core.spel.MyCachedExpressionEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 分布式锁切面
 *
 * @author lvweijie
 * @date 2023/11/11 18:06
 */
@Aspect
@Slf4j
public class DistributedLockAspect implements ApplicationContextAware {

  @Resource
  private DistributedLockFactory distributedLockFactory;

  @Value("${spring.application.name:unknown}")
  private String applicationName;

  private static final String LOCK_PREFIX = "LOCK:";

  private ApplicationContext applicationContext;

  @Around("@annotation(dLock)")
  public Object around(ProceedingJoinPoint joinPoint, DistributedLock dLock) throws Throwable {
    if (!(joinPoint.getSignature() instanceof MethodSignature)) {
      return joinPoint.proceed();
    }
    Object lockKey = getLockKey(joinPoint, dLock);
    IDistributedLock distributedLock = distributedLockFactory.newLock(lockKey, dLock.type());
    return distributedLock.tryLock(dLock.waitTime(), dLock.leaseTime(), dLock.timeUnit(), dLock.msg(), joinPoint::proceed);
  }

  private Object getLockKey(ProceedingJoinPoint joinPoint, DistributedLock dLock) {
    String lockName = dLock.value();
    Assert.hasText(lockName, "@DistributedLock value must not be null or empty");

    lockName = applicationName + ":" + LOCK_PREFIX + lockName;
    Object lockKey;
    if (StringUtils.isNotBlank(dLock.param())) {
      Object evalResult = evalLockParam(joinPoint, dLock.param());
      Assert.isTrue(!ObjectUtils.isEmpty(evalResult), "@DistributedLock param eval failed or invalid");
      if (evalResult instanceof Collection) { //表达式解析反回集合
        List<String> list = new ArrayList<>();
        for (String s : ((Collection<String>) evalResult)) {
          list.add(lockName + ":" + s);
        }
        if (list.isEmpty()) {
          lockKey = lockName;
        } else {
          lockKey = list;
        }
      } else {
        lockKey = lockName + ":" + evalResult;
      }
    } else {
      lockKey = lockName;
    }
    return lockKey;
  }

  /**
   * 计算参数表达式
   *
   * @param point     ProceedingJoinPoint
   * @param lockParam lockParam
   * @return 结果
   */
  private Object evalLockParam(ProceedingJoinPoint point, String lockParam) {
    MethodSignature ms = (MethodSignature) point.getSignature();
    Method method = ms.getMethod();
    Object[] args = point.getArgs();
    Object target = point.getTarget();
    Class<?> targetClass = target.getClass();
    EvaluationContext context = MyCachedExpressionEvaluator.INSTANCE.createContext(method, args, target, targetClass, applicationContext);
    AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
    return MyCachedExpressionEvaluator.INSTANCE.eval(lockParam, elementKey, context);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
