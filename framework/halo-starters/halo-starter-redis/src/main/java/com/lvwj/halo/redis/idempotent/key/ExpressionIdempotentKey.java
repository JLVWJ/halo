package com.lvwj.halo.redis.idempotent.key;

import com.lvwj.halo.core.spel.MyCachedExpressionEvaluator;
import com.lvwj.halo.redis.idempotent.Idempotent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;


public class ExpressionIdempotentKey implements IdempotentKey, ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public String resolver(JoinPoint joinPoint, Idempotent idempotent) {
    return eval(joinPoint, idempotent.spEl());
  }

  private String eval(JoinPoint point, String el) {
    MethodSignature ms = (MethodSignature) point.getSignature();
    Method method = ms.getMethod();
    Object[] args = point.getArgs();
    Object target = point.getTarget();
    Class<?> targetClass = target.getClass();
    EvaluationContext context = MyCachedExpressionEvaluator.INSTANCE.createContext(method, args, target, targetClass, applicationContext);
    AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
    return MyCachedExpressionEvaluator.INSTANCE.evalAsText(el, elementKey, context);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
