package com.lvwj.halo.core.design.pipeline;

import com.lvwj.halo.common.exceptions.BusinessException;
import com.lvwj.halo.common.models.IContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 管道执行器
 *
 * @author lvweijie
 * @date 2022-04-22 09:23
 */
@Slf4j
public class PipelineExecutor {
  @Autowired
  private PipelineRouter pipelineRouter;

  public <T extends IContext> void apply(T ctx) {
    Class<?> targetClass = AopUtils.getTargetClass(ctx);
    List<IPipelineHandler<?>> handlers = pipelineRouter.getHandlers(targetClass);
    if (CollectionUtils.isEmpty(handlers)) {
      String errorLog = String.format("上下文[%s]不存在管道处理器！", targetClass.getSimpleName());
      log.warn(errorLog);
      return;
    }

    for (IPipelineHandler<?> handler : handlers) {
      try {
        IPipelineHandler<T> pipelineHandler = (IPipelineHandler<T>) handler;
        if (pipelineHandler.condition(ctx)) {
          pipelineHandler.handle(ctx);
        }
      } catch (BusinessException ex) {
        log.error(String.format("管道处理器[%s]处理异常:%s", handler.getClass().getSimpleName(), ex));
        throw ex;
      } catch (Exception ex) {
        String errorLog = String.format("管道处理器[%s]处理异常:%s", handler.getClass().getSimpleName(), ex.getMessage());
        log.error(errorLog, ex);
        throw new RuntimeException(errorLog);
      }
    }
  }
}
