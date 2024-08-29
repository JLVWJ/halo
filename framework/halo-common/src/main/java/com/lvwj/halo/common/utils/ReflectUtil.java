package com.lvwj.halo.common.utils;

import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射工具类
 */
public class ReflectUtil extends ReflectionUtils {

  private static final String CGLIB_CLASS_SEPARATOR = "$$";

  /**
   * 获取 Bean 的所有 get方法
   *
   * @param type 类
   * @return PropertyDescriptor数组
   */
  public static PropertyDescriptor[] getBeanGetters(Class type) {
    return getPropertiesHelper(type, true, false);
  }

  /**
   * 获取 Bean 的所有 set方法
   *
   * @param type 类
   * @return PropertyDescriptor数组
   */
  public static PropertyDescriptor[] getBeanSetters(Class type) {
    return getPropertiesHelper(type, false, true);
  }

  /**
   * 获取 Bean 的所有 PropertyDescriptor
   *
   * @param type  类
   * @param read  读取方法
   * @param write 写方法
   * @return PropertyDescriptor数组
   */
  public static PropertyDescriptor[] getPropertiesHelper(Class type, boolean read, boolean write) {
    try {
      PropertyDescriptor[] all = BeanUtil.getPropertyDescriptors(type);
      if (read && write) {
        return all;
      } else {
        List<PropertyDescriptor> properties = new ArrayList<>(all.length);
        for (PropertyDescriptor pd : all) {
          if (read && pd.getReadMethod() != null) {
            properties.add(pd);
          } else if (write && pd.getWriteMethod() != null) {
            properties.add(pd);
          }
        }
        return properties.toArray(new PropertyDescriptor[0]);
      }
    } catch (BeansException ex) {
      throw new CodeGenerationException(ex);
    }
  }

  /**
   * 获取 bean 的属性信息
   *
   * @param propertyType 类型
   * @param propertyName 属性名
   * @return {Property}
   */
  @Nullable
  public static Property getProperty(Class<?> propertyType, String propertyName) {
    PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(propertyType,
            propertyName);
    if (propertyDescriptor == null) {
      return null;
    }
    return ReflectUtil.getProperty(propertyType, propertyDescriptor, propertyName);
  }

  /**
   * 获取 bean 的属性信息
   *
   * @param propertyType       类型
   * @param propertyDescriptor PropertyDescriptor
   * @param propertyName       属性名
   * @return {Property}
   */
  public static Property getProperty(Class<?> propertyType, PropertyDescriptor propertyDescriptor,
                                     String propertyName) {
    Method readMethod = propertyDescriptor.getReadMethod();
    Method writeMethod = propertyDescriptor.getWriteMethod();
    return new Property(propertyType, readMethod, writeMethod, propertyName);
  }

  /**
   * 获取 bean 的属性信息
   *
   * @param propertyType 类型
   * @param propertyName 属性名
   * @return {Property}
   */
  @Nullable
  public static TypeDescriptor getTypeDescriptor(Class<?> propertyType, String propertyName) {
    Property property = ReflectUtil.getProperty(propertyType, propertyName);
    if (property == null) {
      return null;
    }
    return new TypeDescriptor(property);
  }

  /**
   * 获取 类属性信息
   *
   * @param propertyType       类型
   * @param propertyDescriptor PropertyDescriptor
   * @param propertyName       属性名
   * @return {Property}
   */
  public static TypeDescriptor getTypeDescriptor(Class<?> propertyType,
                                                 PropertyDescriptor propertyDescriptor, String propertyName) {
    Method readMethod = propertyDescriptor.getReadMethod();
    Method writeMethod = propertyDescriptor.getWriteMethod();
    Property property = new Property(propertyType, readMethod, writeMethod, propertyName);
    return new TypeDescriptor(property);
  }

  /**
   * 获取 类属性
   *
   * @param clazz     类信息
   * @param fieldName 属性名
   * @return Field
   */
  @Nullable
  public static Field getField(Class<?> clazz, String fieldName) {
    while (clazz != Object.class) {
      try {
        return clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    }
    return null;
  }

  /**
   * 获取 所有 field 属性上的注解
   *
   * @param clazz           类
   * @param fieldName       属性名
   * @param annotationClass 注解
   * @param <T>             注解泛型
   * @return 注解
   */
  @Nullable
  public static <T extends Annotation> T getAnnotation(Class<?> clazz, String fieldName,
                                                       Class<T> annotationClass) {
    Field field = ReflectUtil.getField(clazz, fieldName);
    if (field == null) {
      return null;
    }
    return field.getAnnotation(annotationClass);
  }

  @Nullable
  public static Class<?> getUserClass(Object instance) {
    Assert.notNull(instance, "Instance must not be null");
    Class clazz = instance.getClass();
    if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
      Class<?> superClass = clazz.getSuperclass();
      if (superClass != null && !Object.class.equals(superClass)) {
        return superClass;
      }
    }
    return clazz;
  }

  /**
   * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处 如无法找到, 返回Object.class. eg.
   *
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> Class<T> getClassGenericType(final Class clazz) {
    return getClassGenericType(clazz, 0);
  }

  /**
   * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
   *
   * @param clazz
   * @param index
   * @return
   */
  public static Class getClassGenericType(final Class clazz, final int index) {
    Type genType = clazz.getGenericSuperclass();
    if (!(genType instanceof ParameterizedType)) {
      return Object.class;
    }
    Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
    if (index >= params.length || index < 0) {
      return Object.class;
    }
    if (!(params[index] instanceof Class)) {
      return Object.class;
    }
    return (Class<?>) params[index];
  }

  /**
   * <p>
   * 请仅在确定类存在的情况下调用该方法
   * </p>
   *
   * @param name 类名称
   * @return 返回转换后的 Class
   */
  @SneakyThrows
  public static Class<?> toClassConfident(String name) {
    try {
      return Class.forName(name, false, getDefaultClassLoader());
    } catch (ClassNotFoundException e) {
      try {
        return Class.forName(name);
      } catch (ClassNotFoundException ex) {
        throw new ClassNotFoundException(
                "找不到指定的class！请仅在明确确定会有 class 的时候，调用该方法:" + ex.getMessage());
      }
    }
  }

  public static ClassLoader getDefaultClassLoader() {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    } catch (Throwable ex) {
      // Cannot access thread context ClassLoader - falling back...
    }
    if (cl == null) {
      // No thread context class loader -> use class loader of this class.
      cl = ClassUtils.class.getClassLoader();
      if (cl == null) {
        // getClassLoader() returning null indicates the bootstrap ClassLoader
        try {
          cl = ClassLoader.getSystemClassLoader();
        } catch (Throwable ex) {
          // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
        }
      }
    }
    return cl;
  }
}
