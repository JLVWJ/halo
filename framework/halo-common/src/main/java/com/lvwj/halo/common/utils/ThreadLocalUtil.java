package com.lvwj.halo.common.utils;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * ThreadLocal 工具类
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-04 11:50
 */
public class ThreadLocalUtil {

  private static final ThreadLocal<Map<String, Object>> LOCAL = TransmittableThreadLocal.withInitial(HashMap::new);

  /**
   * @return threadLocal中的全部值
   */
  public static Map<String, Object> getAll() {
    return new HashMap<>(LOCAL.get());
  }

  /**
   * 设置一个值到ThreadLocal
   *
   * @param key   键
   * @param value 值
   * @param <T>   值的类型
   * @see Map#put(Object, Object)
   */
  public static <T> void putIfNotEmpty(String key, T value) {
    if (ObjectUtil.isNotEmpty(value)) {
      put(key, value);
    }
  }

  public static <T> void put(String key, T value) {
    LOCAL.get().put(key, value);
  }

  /**
   * 设置一个值到ThreadLocal
   *
   * @param map map
   * @return 被放入的值
   * @see Map#putAll(Map)
   */
  public static void put(Map<String, Object> map) {
    LOCAL.get().putAll(map);
  }

  /**
   * 删除参数对应的值
   *
   * @param key
   * @see Map#remove(Object)
   */
  public static void remove(String key) {
    LOCAL.get().remove(key);
  }

  /**
   * 清空ThreadLocal
   *
   * @see Map#clear()
   */
  public static void clear() {
    LOCAL.remove();
  }

  /**
   * 从ThreadLocal中获取值
   *
   * @param key 键
   * @param <T> 值泛型
   * @return 值, 不存在则返回null, 如果类型与泛型不一致, 可能抛出{@link ClassCastException}
   * @see Map#get(Object)
   * @see ClassCastException
   */
  @Nullable
  public static <T> T get(String key) {
    return ((T) LOCAL.get().get(key));
  }

  /**
   * 从ThreadLocal中获取值,并指定一个当值不存在的提供者
   *
   * @see Supplier
   */
  @Nullable
  public static <T> T getIfAbsent(String key, Supplier<T> supplierOnNull) {
    return ((T) LOCAL.get().computeIfAbsent(key, k -> supplierOnNull.get()));
  }

  /**
   * 获取一个值后然后删除掉
   *
   * @param key 键
   * @param <T> 值类型
   * @return 值, 不存在则返回null
   * @see this#get(String)
   * @see this#remove(String)
   */
  public static <T> T getAndRemove(String key) {
    try {
      return get(key);
    } finally {
      remove(key);
    }
  }

  /**
   * 设置当前用户
   *
   * @author lvweijie
   * @date 2024/8/31 17:58
   * @param currentUser 当前用户
   */
  @Deprecated
  public static void putCurrentUser(String currentUser) {
    putIfNotEmpty(CURRENT_USERNAME, currentUser);
  }

  /**
   * 获取当前用户
   *
   * @author lvweijie
   * @date 2024/8/31 17:58
   * @return java.lang.String
   */
  @Deprecated
  public static String getCurrentUser() {
    return get(CURRENT_USERNAME);
  }

  /**
   * 设置当前用户名称
   *
   * @author lvweijie
   * @date 2024/8/31 17:58
   * @param currentUser 当前用户
   */
  public static void putCurrentUserName(String currentUser) {
    putIfNotEmpty(CURRENT_USERNAME, currentUser);
  }

  /**
   * 获取当前用户名称
   *
   * @author lvweijie
   * @date 2024/8/31 17:58
   * @return java.lang.String
   */
  public static String getCurrentUserName() {
    return get(CURRENT_USERNAME);
  }

  /**
   * 设置当前用户ID
   *
   * @author lvweijie
   * @date 2024/8/31 17:58
   * @param userId 当前用户
   */
  public static void putCurrentUserId(Long userId) {
    putIfNotEmpty(CURRENT_USERID, userId);
  }

  /**
   * 获取当前用户ID
   *
   * @author lvweijie
   * @date 2024/8/31 17:58
   * @return java.lang.String
   */
  public static Long getCurrentUserId() {
    return get(CURRENT_USERID);
  }

  private static final String CURRENT_USERNAME = "currentUserName";
  private static final String CURRENT_USERID = "currentUserId";

}
