package com.lvwj.halo.core.design.pipeline;

import com.lvwj.halo.common.models.IContext;
import org.springframework.core.Ordered;

/**
 * 管道处理器
 * 一个管道(流程)会对应多个管道处理器(步骤)，处理器间用上下文传递数据
 *
 * @author lvweijie
 * @date 2022-04-22 00:43
 */
public interface IPipelineHandler<T extends IContext> extends Ordered {

  /**
   * 条件为true时，才会执行handle方法
   */
  boolean condition(T ctx);

  void handle(T ctx);

  @Override
  int getOrder();
}