package com.lvwj.halo.core.spel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * ExpressionRootObject
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-14 16:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionRootObject {

  private Method method;

  private Object[] args;

  private Object target;

  private Class<?> targetClass;

  private Method targetMethod;
}
