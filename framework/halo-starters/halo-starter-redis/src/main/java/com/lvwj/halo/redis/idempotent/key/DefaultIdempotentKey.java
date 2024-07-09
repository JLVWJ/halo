package com.lvwj.halo.redis.idempotent.key;

import cn.hutool.crypto.SecureUtil;
import com.lvwj.halo.redis.idempotent.Idempotent;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;

public class DefaultIdempotentKey implements IdempotentKey {

  /**
   * 全类名+方法名+参数
   *
   * @author lvwj
   * @date 2022-08-18 11:11
   */
  @Override
  public String resolver(JoinPoint joinPoint, Idempotent idempotent) {
    String typeName = joinPoint.getSignature().getDeclaringTypeName();
    String methodName = joinPoint.getSignature().toString();
    String argsStr = StringUtils.join(joinPoint.getArgs(),',');
    return SecureUtil.md5(typeName + methodName + argsStr);
  }
}
