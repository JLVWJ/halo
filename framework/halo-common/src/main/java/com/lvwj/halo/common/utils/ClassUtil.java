package com.lvwj.halo.common.utils;

import lombok.SneakyThrows;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

import static com.lvwj.halo.common.function.Streams.filterAll;
import static com.lvwj.halo.common.utils.CollectionUtil.ofImmutableSet;
import static java.util.Collections.unmodifiableSet;

/**
 * 类操作工具
 *
 * @author L.cm
 */
public class ClassUtil extends ClassUtils {

  private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

  /**
   * 获取方法参数信息
   *
   * @param constructor    构造器
   * @param parameterIndex 参数序号
   * @return {MethodParameter}
   */
  public static MethodParameter getMethodParameter(Constructor<?> constructor, int parameterIndex) {
    MethodParameter methodParameter = new SynthesizingMethodParameter(constructor, parameterIndex);
    methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
    return methodParameter;
  }

  /**
   * 获取方法参数信息
   *
   * @param method         方法
   * @param parameterIndex 参数序号
   * @return {MethodParameter}
   */
  public static MethodParameter getMethodParameter(Method method, int parameterIndex) {
    MethodParameter methodParameter = new SynthesizingMethodParameter(method, parameterIndex);
    methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
    return methodParameter;
  }

  /**
   * 获取Annotation
   *
   * @param method         Method
   * @param annotationType 注解类
   * @param <A>            泛型标记
   * @return {Annotation}
   */
  public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
    Class<?> targetClass = method.getDeclaringClass();
    // The method may be on an interface, but we need attributes from the target class.
    // If the target class is null, the method will be unchanged.
    Method specificMethod = getMostSpecificMethod(method, targetClass);
    // If we are dealing with method with generic parameters, find the original method.
    specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
    // 先找方法，再找方法上的类
    A annotation = AnnotatedElementUtils.findMergedAnnotation(specificMethod, annotationType);
    if (null != annotation) {
      return annotation;
    }
    // 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
    return AnnotatedElementUtils.findMergedAnnotation(specificMethod.getDeclaringClass(), annotationType);
  }


  /**
   * 判断是否有注解 Annotation
   *
   * @param method         Method
   * @param annotationType 注解类
   * @param <A>            泛型标记
   * @return {boolean}
   */
  public static <A extends Annotation> boolean isAnnotated(Method method, Class<A> annotationType) {
    // 先找方法，再找方法上的类
    boolean isMethodAnnotated = AnnotatedElementUtils.isAnnotated(method, annotationType);
    if (isMethodAnnotated) {
      return true;
    }
    // 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
    Class<?> targetClass = method.getDeclaringClass();
    return AnnotatedElementUtils.isAnnotated(targetClass, annotationType);
  }


  public static String getClassPath() {
    return FileUtil.normalize(getClassPathURL().getPath());
  }

  public static URL getClassPathURL() {
    return getResourceURL("");
  }

  @SneakyThrows
  public static URL getResourceURL(String resource) {
    return ResourceUtil.getResourceURL(resource);
  }

  /**
   * Map with primitive type name as key and corresponding primitive type as
   * value, for example: "int" -> "int.class".
   */
  private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP = new HashMap<String, Class<?>>(32);
  /**
   * Map with primitive wrapper type as key and corresponding primitive type
   * as value, for example: Integer.class -> int.class.
   */
  private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_TYPE_MAP = new HashMap<Class<?>, Class<?>>(16);

  /**
   * Simple Types including:
   * <ul>
   *     <li>{@link Void}</li>
   *     <li>{@link Boolean}</li>
   *     <li>{@link Character}</li>
   *     <li>{@link Byte}</li>
   *     <li>{@link Integer}</li>
   *     <li>{@link Float}</li>
   *     <li>{@link Double}</li>
   *     <li>{@link String}</li>
   *     <li>{@link BigDecimal}</li>
   *     <li>{@link BigInteger}</li>
   *     <li>{@link Date}</li>
   *     <li>{@link Object}</li>
   * </ul>
   *
   * @see javax.management.openmbean.SimpleType
   * @since 2.7.6
   */
  public static final Set<Class<?>> SIMPLE_TYPES = ofImmutableSet(
          Void.class,
          Boolean.class,
          Character.class,
          Byte.class,
          Short.class,
          Integer.class,
          Long.class,
          Float.class,
          Double.class,
          String.class,
          BigDecimal.class,
          BigInteger.class,
          Date.class,
          Object.class
  );

  static {
    PRIMITIVE_WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
    PRIMITIVE_WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
    PRIMITIVE_WRAPPER_TYPE_MAP.put(Character.class, char.class);
    PRIMITIVE_WRAPPER_TYPE_MAP.put(Double.class, double.class);
    PRIMITIVE_WRAPPER_TYPE_MAP.put(Float.class, float.class);
    PRIMITIVE_WRAPPER_TYPE_MAP.put(Integer.class, int.class);
    PRIMITIVE_WRAPPER_TYPE_MAP.put(Long.class, long.class);
    PRIMITIVE_WRAPPER_TYPE_MAP.put(Short.class, short.class);

    Set<Class<?>> primitiveTypeNames = new HashSet<>(32);
    primitiveTypeNames.addAll(PRIMITIVE_WRAPPER_TYPE_MAP.values());
    primitiveTypeNames.addAll(Arrays
            .asList(boolean[].class, byte[].class, char[].class, double[].class,
                    float[].class, int[].class, long[].class, short[].class));
    for (Class<?> primitiveTypeName : primitiveTypeNames) {
      PRIMITIVE_TYPE_NAME_MAP.put(primitiveTypeName.getName(), primitiveTypeName);
    }
  }

  /**
   * get class loader
   *
   * @param clazz
   * @return class loader
   */
  public static ClassLoader getClassLoader(Class<?> clazz) {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    } catch (Throwable ex) {
      // Cannot access thread context ClassLoader - falling back to system class loader...
    }
    if (cl == null) {
      // No thread context class loader -> use class loader of this class.
      cl = clazz.getClassLoader();
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

  /**
   * Return the default ClassLoader to use: typically the thread context
   * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
   * class will be used as fallback.
   * <p>
   * Call this method if you intend to use the thread context ClassLoader in a
   * scenario where you absolutely need a non-null ClassLoader reference: for
   * example, for class path resource loading (but not necessarily for
   * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
   * reference as well).
   *
   * @return the default ClassLoader (never <code>null</code>)
   * @see java.lang.Thread#getContextClassLoader()
   */
  public static ClassLoader getClassLoader() {
    return getClassLoader(ClassUtil.class);
  }

  /**
   * Resolve the given class name as primitive class, if appropriate,
   * according to the JVM's naming rules for primitive classes.
   * <p>
   * Also supports the JVM's internal class names for primitive arrays. Does
   * <i>not</i> support the "[]" suffix notation for primitive arrays; this is
   * only supported by {@link #forName}.
   *
   * @param name the name of the potentially primitive class
   * @return the primitive class, or <code>null</code> if the name does not
   * denote a primitive class or primitive array class
   */
  public static Class<?> resolvePrimitiveClassName(String name) {
    Class<?> result = null;
    // Most class names will be quite long, considering that they
    // SHOULD sit in a package, so a length check is worthwhile.
    if (name != null && name.length() <= 8) {
      // Could be a primitive - likely.
      result = (Class<?>) PRIMITIVE_TYPE_NAME_MAP.get(name);
    }
    return result;
  }

  public static String toShortString(Object obj) {
    if (obj == null) {
      return "null";
    }
    return obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj);
  }

  public static String simpleClassName(Class<?> clazz) {
    if (clazz == null) {
      throw new NullPointerException("clazz");
    }
    String className = clazz.getName();
    final int lastDotIdx = className.lastIndexOf(StringPool.DOT);
    if (lastDotIdx > -1) {
      return className.substring(lastDotIdx + 1);
    }
    return className;
  }


  /**
   * The specified type is primitive type or simple type
   *
   * @param type the type to test
   * @return
   * @deprecated as 2.7.6, use {@link Class#isPrimitive()} plus {@link #isSimpleType(Class)} instead
   */
  public static boolean isPrimitive(Class<?> type) {
    return type != null && (type.isPrimitive() || isSimpleType(type));
  }

  /**
   * The specified type is simple type or not
   *
   * @param type the type to test
   * @return if <code>type</code> is one element of {@link #SIMPLE_TYPES}, return <code>true</code>, or <code>false</code>
   * @see #SIMPLE_TYPES
   * @since 2.7.6
   */
  public static boolean isSimpleType(Class<?> type) {
    return SIMPLE_TYPES.contains(type);
  }

  public static Object convertPrimitive(Class<?> type, String value) {
    if (value == null) {
      return null;
    } else if (type == char.class || type == Character.class) {
      return value.length() > 0 ? value.charAt(0) : '\0';
    } else if (type == boolean.class || type == Boolean.class) {
      return Boolean.valueOf(value);
    }
    try {
      if (type == byte.class || type == Byte.class) {
        return Byte.valueOf(value);
      } else if (type == short.class || type == Short.class) {
        return Short.valueOf(value);
      } else if (type == int.class || type == Integer.class) {
        return Integer.valueOf(value);
      } else if (type == long.class || type == Long.class) {
        return Long.valueOf(value);
      } else if (type == float.class || type == Float.class) {
        return Float.valueOf(value);
      } else if (type == double.class || type == Double.class) {
        return Double.valueOf(value);
      }
    } catch (NumberFormatException e) {
      return null;
    }
    return value;
  }

  /**
   * Get all super classes from the specified type
   *
   * @param type         the specified type
   * @param classFilters the filters for classes
   * @return non-null read-only {@link Set}
   * @since 2.7.6
   */
  public static Set<Class<?>> getAllSuperClasses(Class<?> type, Predicate<Class<?>>... classFilters) {

    Set<Class<?>> allSuperClasses = new LinkedHashSet<>();

    Class<?> superClass = type.getSuperclass();

    if (superClass != null) {
      // add current super class
      allSuperClasses.add(superClass);
      // add ancestor classes
      allSuperClasses.addAll(getAllSuperClasses(superClass));
    }

    return unmodifiableSet(filterAll(allSuperClasses, classFilters));
  }

  /**
   * the semantics is same as {@link Class#isAssignableFrom(Class)}
   *
   * @param superType  the super type
   * @param targetType the target type
   * @return see {@link Class#isAssignableFrom(Class)}
   * @since 2.7.6
   */
  public static boolean isAssignableFrom(Class<?> superType, Class<?> targetType) {
    // any argument is null
    if (superType == null || targetType == null) {
      return false;
    }
    // equals
    if (Objects.equals(superType, targetType)) {
      return true;
    }
    // isAssignableFrom
    return superType.isAssignableFrom(targetType);
  }

  /**
   * Test the specified class name is present in the {@link ClassLoader}
   *
   * @param className   the name of {@link Class}
   * @param classLoader {@link ClassLoader}
   * @return If found, return <code>true</code>
   * @since 2.7.6
   */
  public static boolean isPresent(String className, ClassLoader classLoader) {
    try {
      forName(className, classLoader);
    } catch (Throwable ignored) { // Ignored
      return false;
    }
    return true;
  }

  /**
   * Resolve the {@link Class} by the specified name and {@link ClassLoader}
   *
   * @param className   the name of {@link Class}
   * @param classLoader {@link ClassLoader}
   * @return If can't be resolved , return <code>null</code>
   * @since 2.7.6
   */
  public static Class<?> resolveClass(String className, ClassLoader classLoader) {
    Class<?> targetClass = null;
    try {
      targetClass = forName(className, classLoader);
    } catch (Throwable ignored) { // Ignored
    }
    return targetClass;
  }

  /**
   * Is generic class or not?
   *
   * @param type the target type
   * @return if the target type is not null or <code>void</code> or Void.class, return <code>true</code>, or false
   * @since 2.7.6
   */
  public static boolean isGenericClass(Class<?> type) {
    return type != null && !void.class.equals(type) && !Void.class.equals(type);
  }

}
