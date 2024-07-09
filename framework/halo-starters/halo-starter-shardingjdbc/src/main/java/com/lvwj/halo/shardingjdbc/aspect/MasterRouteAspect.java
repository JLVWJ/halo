package com.lvwj.halo.shardingjdbc.aspect;

import com.lvwj.halo.common.utils.Exceptions;
import com.lvwj.halo.shardingjdbc.annotation.Master;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author lvweijie
 * @date 2023年12月12日 10:31
 */
@Aspect
@Component
public class MasterRouteAspect {

    @Around("@annotation(master)")
    public Object masterRoute(ProceedingJoinPoint point, Master master) {
        if (!HintManager.isWriteRouteOnly()) {
            HintManager.clear();
            HintManager hintManager = HintManager.getInstance();
            hintManager.setWriteRouteOnly();
        }
        try {
            return point.proceed();
        } catch (Throwable e) {
            throw Exceptions.unchecked(e);
        } finally {
            HintManager.clear();
        }
    }
}
