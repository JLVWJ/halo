package com.lvwj.halo.extension;

import cn.hutool.extra.spring.SpringUtil;
import com.lvwj.halo.common.constants.ExtensionConstant;
import com.lvwj.halo.common.models.BusinessScenario;
import com.lvwj.halo.extension.annotation.Extension;
import com.lvwj.halo.extension.annotation.Extensions;
import com.lvwj.halo.extension.annotation.SPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


@Slf4j
public class ExtensionLoader<T> {

  // 所有的扩展点加载器，初始化之后缓存起来，提高访问效率。每个扩展点一个加载器实例
  private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

  // 扩展实现map, value按照
  private final Map<BusinessScenario, List<T>> extensionInfoMap = new LinkedHashMap<>();

  /**
   * 私有构造器，不允许直接new
   */
  private ExtensionLoader(Class<T> type) {
    if (!withSPIAnnotation(type)) {
      throw new IllegalArgumentException("Type (" + type + ") should be annotated with @SPI !");
    }

    Map<String, T> beanMap = SpringUtil.getBeansOfType(type);
    if (CollectionUtils.isEmpty(beanMap)) {
      log.warn("empty extension with type [{}]", type.getName());
      return;
    }

    // 转为List并按order排序
    List<T> orderedExtensionList = sort(beanMap);
    orderedExtensionList.forEach(this::registerExtension);

    //打印扩展实现列表
    printExtensionList(type);
    // 检测场景是否冲突，此处打印主要是在打印之后更便于排查
    checkRepeatable(type);
  }

  /**
   * 获取对应扩展节点的加载器，每个扩展节点有自己独立的加载器
   */
  public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
    if (type == null) {
      throw new IllegalArgumentException("Extension type == null");
    }

    ExtensionLoader<T> loader = (ExtensionLoader) EXTENSION_LOADERS.get(type);
    if (loader == null) {
      synchronized (type) {
        loader = (ExtensionLoader) EXTENSION_LOADERS.computeIfAbsent(type, (spiType) -> new ExtensionLoader(spiType));
      }
    }

    return loader;
  }

  public static <T> T getExtension(Class<T> type, String scenario) {
    BusinessScenario businessScenario = BusinessScenario.of(ExtensionConstant.DEFAULT_BUSINESS,
            ExtensionConstant.DEFAULT_USE_CASE, scenario);

    return getExtension(type, businessScenario, false);
  }

  public static <T> T getExtension(Class<T> type, BusinessScenario scenario) {
    return getExtension(type, scenario, false);
  }

  public static <T> T getExtension(Class<T> type, BusinessScenario scenario, boolean failOver) {
    List<T> extList = getExtensionList(type, scenario, failOver);
    if (CollectionUtils.isEmpty(extList)) {
      return null;
    }
    return extList.get(0);
  }

  /**
   * 根据场景获取对应的扩展实现列表，不支持容错降级
   */
  public static <T> List<T> getExtensionList(Class<T> type, BusinessScenario scenario) {
    return getExtensionList(type, scenario, false);
  }

  /**
   * 根据场景获取对应的扩展实现列表
   */
  public static <T> List<T> getExtensionList(Class<T> type, BusinessScenario scenario, boolean failOver) {
    List<T> extensionList = getExtensionLoader(type).extensionInfoMap.get(scenario);
    if (!CollectionUtils.isEmpty(extensionList)) {
      return extensionList;
    }
    // 如果不需要降级，直接返回
    if (!failOver) {
      return null;
    }
    return getFailOverExtensionList(type, scenario);
  }

  public static <T> List<T> getFailOverExtensionList(Class<T> type, BusinessScenario scenario) {
    // business + useCase + 默认场景
    scenario = BusinessScenario.of(scenario.getBusiness(), scenario.getUseCase());
    List<T> extensionList = getExtensionLoader(type).extensionInfoMap.get(scenario);
    if (!CollectionUtils.isEmpty(extensionList)) {
      return extensionList;
    }

    // business + 默认useCase + 默认scenario
    scenario = BusinessScenario.of(scenario.getBusiness());
    extensionList = getExtensionLoader(type).extensionInfoMap.get(scenario);
    if (!CollectionUtils.isEmpty(extensionList)) {
      return extensionList;
    }

    // 采用全默认的进行之星
    scenario = BusinessScenario.of();
    return getExtensionLoader(type).extensionInfoMap.get(scenario);
  }

  /**
   * 判断是否声明为扩展点
   */
  private static <T> boolean withSPIAnnotation(Class<T> type) {
    return type.isAnnotationPresent(SPI.class);
  }

  private void checkRepeatable(Class<?> type) {
    SPI spi = type.getAnnotation(SPI.class);
    // 允许重复不需要检测
    if (spi.repeatable()) {
      return;
    }
    extensionInfoMap.values().forEach(extList -> Assert.isTrue(extList.size() <= 1, "scenario cannot repeat!"));
  }

  /**
   * 打印扩展实现列表
   */
  private void printExtensionList(Class<T> type) {
    log.info("|──  {}", type.getName());
    extensionInfoMap.keySet().forEach(scenario -> {
      log.info("|    |── {}", scenario);
      extensionInfoMap.get(scenario).forEach(instance -> log.info("|    |    |── {}", instance.getClass().getName()));
    });
  }

  /**
   * 实现节点按order排序，数值越小越靠前
   */
  private List<T> sort(Map<String, T> beanMap) {
    return beanMap.values().stream().sorted(this::compareExtensionOrder).collect(Collectors.toList());
  }

  private int compareExtensionOrder(T o1, T o2) {
    Class<?> targetClass1 = AopUtils.getTargetClass(o1);
    Class<?> targetClass2 = AopUtils.getTargetClass(o2);
    Extension extension1 = AnnotationUtils.findAnnotation(targetClass1, Extension.class);
    Extensions extensions1 = AnnotationUtils.findAnnotation(targetClass1, Extensions.class);
    Extension extension2 = AnnotationUtils.findAnnotation(targetClass2, Extension.class);
    Extensions extensions2 = AnnotationUtils.findAnnotation(targetClass2, Extensions.class);
    if (null == extension1 && null == extensions1) {
      throw new RuntimeException(targetClass1.getName() + " should be annotated with @Extension or @Extensions");
    }
    if (null == extension2 && null == extensions2) {
      throw new RuntimeException(targetClass2.getName() + " should be annotated with @Extension or @Extensions");
    }
    if (null == extension1) {
      if (null == extension2) {
        return extensions1.order() - extensions2.order();
      } else {
        return extensions1.order() - extension2.order();
      }
    } else {
      if (null == extension2) {
        return extension1.order() - extensions2.order();
      } else {
        return extension1.order() - extension2.order();
      }
    }
  }

  private void registerExtension(T extensionInstance) {
    Class<?> targetClass = AopUtils.getTargetClass(extensionInstance);
    Extension extension = AnnotationUtils.findAnnotation(targetClass, Extension.class);
    if (null != extension) {
      BusinessScenario businessScenario = BusinessScenario.of(extension.business(), extension.useCase(), extension.scenario());
      addExtensionToMap(businessScenario, extensionInstance);
    }
    Extensions extensions = AnnotationUtils.findAnnotation(targetClass, Extensions.class);
    if (null != extensions) {
      Extension[] value = extensions.value();
      if (null != value) {
        for (Extension ext : extensions.value()) {
          BusinessScenario businessScenario = BusinessScenario.of(ext.business(), ext.useCase(), ext.scenario());
          addExtensionToMap(businessScenario, extensionInstance);
        }
      }
      if (!isDefault(extensions.business(), ExtensionConstant.DEFAULT_BUSINESS)
              || !isDefault(extensions.useCase(), ExtensionConstant.DEFAULT_USE_CASE)
              || !isDefault(extensions.scenario(), ExtensionConstant.DEFAULT_SCENARIO)) {
        for (String business : extensions.business()) {
          for (String useCase : extensions.useCase()) {
            for (String scenario : extensions.scenario()) {
              BusinessScenario businessScenario = BusinessScenario.of(business, useCase, scenario);
              addExtensionToMap(businessScenario, extensionInstance);
            }
          }
        }
      }
    }
  }

  private Boolean isDefault(String[] arr, String defaultValue) {
    return null != arr && arr.length == 1 && arr[0].equals(defaultValue);
  }

  /**
   * 添加到扩展点分组的map
   */
  private void addExtensionToMap(BusinessScenario scenario, T extensionInstance) {
    List<T> extensionList = extensionInfoMap.computeIfAbsent(scenario, group -> new ArrayList<>());
    extensionList.add(extensionInstance);
  }
}
