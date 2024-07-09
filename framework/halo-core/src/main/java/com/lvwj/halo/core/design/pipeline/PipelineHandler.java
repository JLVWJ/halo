package com.lvwj.halo.core.design.pipeline;

import com.lvwj.halo.common.models.IContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;

import jakarta.annotation.PostConstruct;

/**
 * 抽象管道上下文处理器
 *
 * @author lvweijie
 * @date 2022-04-22 01:17
 */
public abstract class PipelineHandler<T extends IContext> implements IPipelineHandler<T> {

  @Autowired
  private PipelineRouter pipelineRouter;

  @PostConstruct
  public void init() {
    pipelineRouter.putHandler(contextClass(), this);
  }

  protected Class<?> contextClass() {
    Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(this.getClass(), IPipelineHandler.class);
    return null != classes ? classes[0] : null;
  }
}
