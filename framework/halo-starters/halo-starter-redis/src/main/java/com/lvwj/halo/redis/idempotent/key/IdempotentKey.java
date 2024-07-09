package com.lvwj.halo.redis.idempotent.key;

import com.lvwj.halo.redis.idempotent.Idempotent;
import org.aspectj.lang.JoinPoint;

/**
 * 幂等 Key 解析器接口
 *
 * @author lvwj
 * @date 2022-08-17 19:38
 */
public interface IdempotentKey {

    /**
     * 解析一个 Key
     *
     * @param idempotent 幂等注解
     * @param joinPoint  AOP 切面
     * @return Key
     */
    String resolver(JoinPoint joinPoint, Idempotent idempotent);

}
