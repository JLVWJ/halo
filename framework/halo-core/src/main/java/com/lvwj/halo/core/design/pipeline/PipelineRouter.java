package com.lvwj.halo.core.design.pipeline;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管道路由器
 *
 * @author lvweijie
 * @date 2022-04-22 09:23
 */
public class PipelineRouter {

  private final Map<Class<?>, List<IPipelineHandler<?>>> MAP = new HashMap<>();

  public void putHandler(Class<?> clazz, IPipelineHandler<?> handler) {
    List<IPipelineHandler<?>> handlers = MAP.computeIfAbsent(clazz, s -> new ArrayList<>());
    handlers.add(handler);
    //给管道处理器按@Order排序，按数字从小到大顺序
    AnnotationAwareOrderComparator.sort(handlers);
  }

  public List<IPipelineHandler<?>> getHandlers(Class<?> clazz) {
    return MAP.getOrDefault(clazz, new ArrayList<>());
  }
}
