package com.lvwj.halo.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 集合工具类
 */
public class CollectionUtil extends CollectionUtils {

  /**
   * Return {@code true} if the supplied Collection is not {@code null} or empty. Otherwise, return {@code false}.
   *
   * @param collection the Collection to check
   * @return whether the given Collection is not empty
   */
  public static boolean isNotEmpty(@Nullable Collection<?> collection) {
    return !CollectionUtil.isEmpty(collection);
  }

  /**
   * Return {@code true} if the supplied Map is not {@code null} or empty. Otherwise, return {@code false}.
   *
   * @param map the Map to check
   * @return whether the given Map is not empty
   */
  public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
    return !CollectionUtil.isEmpty(map);
  }

  /**
   * Check whether the given Array contains the given element.
   *
   * @param array   the Array to check
   * @param element the element to look for
   * @param <T>     The generic tag
   * @return {@code true} if found, {@code false} else
   */
  public static <T> boolean contains(@Nullable T[] array, final T element) {
    if (array == null) {
      return false;
    }
    return Arrays.stream(array).anyMatch(x -> ObjectUtil.nullSafeEquals(x, element));
  }

  /**
   * Concatenates 2 arrays
   *
   * @param one   数组1
   * @param other 数组2
   * @return 新数组
   */
  public static String[] concat(String[] one, String[] other) {
    return concat(one, other, String.class);
  }

  /**
   * Concatenates 2 arrays
   *
   * @param one   数组1
   * @param other 数组2
   * @param clazz 数组类
   * @return 新数组
   */
  public static <T> T[] concat(T[] one, T[] other, Class<T> clazz) {
    T[] target = (T[]) Array.newInstance(clazz, one.length + other.length);
    System.arraycopy(one, 0, target, 0, one.length);
    System.arraycopy(other, 0, target, one.length, other.length);
    return target;
  }

  /**
   * 对象是否为数组对象
   *
   * @param obj 对象
   * @return 是否为数组对象，如果为{@code null} 返回false
   */
  public static boolean isArray(Object obj) {
    if (null == obj) {
      return false;
    }
    return obj.getClass().isArray();
  }

  /**
   * 不可变 Set
   *
   * @param es  对象
   * @param <E> 泛型
   * @return 集合
   */
  @SafeVarargs
  public static <E> Set<E> ofImmutableSet(E... es) {
    Objects.requireNonNull(es, "args es is null.");
    return Arrays.stream(es).collect(Collectors.toSet());
  }

  /**
   * 不可变 List
   *
   * @param es  对象
   * @param <E> 泛型
   * @return 集合
   */
  @SafeVarargs
  public static <E> List<E> ofImmutableList(E... es) {
    Objects.requireNonNull(es, "args es is null.");
    return Arrays.stream(es).collect(Collectors.toList());
  }

  /**
   * Iterable 转换为List集合
   *
   * @param elements Iterable
   * @param <E>      泛型
   * @return 集合
   */
  public static <E> List<E> toList(Iterable<E> elements) {
    Objects.requireNonNull(elements, "elements es is null.");
    if (elements instanceof Collection) {
      return new ArrayList((Collection) elements);
    }
    Iterator<E> iterator = elements.iterator();
    List<E> list = new ArrayList<>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    return list;
  }

  /**
   * 将key value 数组转为 map
   *
   * @param keysValues key value 数组
   * @param <K>        key
   * @param <V>        value
   * @return map 集合
   */
  public static <K, V> Map<K, V> toMap(Object... keysValues) {
    int kvLength = keysValues.length;
    if (kvLength % 2 != 0) {
      throw new IllegalArgumentException("wrong number of arguments for met, keysValues length can not be odd");
    }
    Map<K, V> keyValueMap = new HashMap<>(kvLength);
    for (int i = kvLength - 2; i >= 0; i -= 2) {
      Object key = keysValues[i];
      Object value = keysValues[i + 1];
      keyValueMap.put((K) key, (V) value);
    }
    return keyValueMap;
  }

  /**
   * 求两个集合交集
   *
   * @param list1 集合1
   * @param list2 集合2
   * @param <T>   泛型
   * @return List
   */
  public static <T> List<T> intersection(List<T> list1, List<T> list2) {
    List<T> list = new ArrayList<>();
    //获取双集合基数
    Map<T, Integer> countMap1 = getCountMap(list1);
    Map<T, Integer> countMap2 = getCountMap(list2);
    //遍历key集合
    Set<T> keySet = new HashSet<>(list1);
    keySet.addAll(list2);
    for (T t : keySet) {
      //交集按小基数次数添加集合
      int m = Math.min(getCount(countMap1, t), getCount(countMap2, t));
      for (int i = 0; i < m; i++) {
        list.add(t);
      }
    }
    return list;
  }

  /**
   * 求两个集合差集
   *
   * @param list1 集合1
   * @param list2 集合2
   * @param <T>   泛型
   * @return List
   */
  public static <T> List<T> disjunction(List<T> list1, List<T> list2) {
    List<T> list = new ArrayList<>();
    //获取双集合基数
    Map<T, Integer> countMap1 = getCountMap(list1);
    Map<T, Integer> countMap2 = getCountMap(list2);
    //遍历key集合
    Set<T> keySet = new HashSet<>(list1);
    keySet.addAll(list2);
    for (T t : keySet) {
      //差集按大基数次数-小基数次数添加集合
      int m = Math.max(getCount(countMap1, t), getCount(countMap2, t)) - Math.min(
              getCount(countMap1, t), getCount(countMap2, t));
      for (int i = 0; i < m; i++) {
        list.add(t);
      }
    }
    return list;
  }

  /**
   * 根据T的equals获取每个t的出现基数
   *
   * @param list 集合
   * @param <T>  泛型
   * @return Map
   */
  private static <T> Map<T, Integer> getCountMap(List<T> list) {
    Map<T, Integer> countMap = new HashMap<>(list.size());
    for (T t : list) {
      countMap.merge(t, 1, Integer::sum);
    }
    return countMap;
  }

  /**
   * 获取T在map中的基数
   *
   * @param countMap 基数map
   * @param t        对象
   * @param <T>      泛型
   * @return 基数
   */
  private static <T> Integer getCount(Map<T, Integer> countMap, T t) {
    Integer count = countMap.get(t);
    if (count == null) {
      return 0;
    }
    return count;
  }

  public static <E> void forEach(Collection<E> collection, Consumer<? super E> action) {
    if (isEmpty(collection) || action == null) {
      return;
    }
    collection.forEach(action);
  }

  public static <F, T> List<T> map(Collection<F> source, Function<F, T> func) {
    if (isEmpty(source)) {
      return Collections.emptyList();
    }
    List<T> resultList = new ArrayList<>(source.size());
    for (F f : source) {
      T t = func.apply(f);
      resultList.add(t);
    }
    return resultList;
  }

  @SuppressWarnings("unchecked")
  public static <F, T> List<T> map(Collection<F> source, Class<T> tcLass) {
    return map(source, r -> {
      T instance = (T) ReflectUtils.newInstance(tcLass);
      BeanUtils.copyProperties(r, instance);
      return instance;
    });
  }

  public static <F, T> List<T> mapNotSame(Collection<F> source, Function<F, T> func) {
    List<T> list = map(source, func);
    HashSet<T> hashSet = new HashSet<>(list);
    list.clear();
    list.addAll(hashSet);
    return list;
  }

  public static <F> String mapJoin(Collection<F> source, Function<F, String> func, String sep) {
    if (isEmpty(source)) {
      return "";
    }
    return source.stream().map(func).distinct().collect(Collectors.joining(sep));
  }

  public static <F> String mapJoin(Collection<F> source, Function<F, String> func) {
    return mapJoin(source, func, ",");
  }

  public static <F, T> List<T> mapIndex(Collection<F> source, BiFunction<F, Integer, T> func) {
    if (isEmpty(source)) {
      return Collections.emptyList();
    }
    List<T> resultList = new ArrayList<>();
    AtomicInteger index = new AtomicInteger();
    for (F f : source) {
      resultList.add(func.apply(f, index.getAndIncrement()));
    }
    return resultList;
  }

  public static <E> List<E> filter(Collection<E> source, Predicate<E> predicate) {
    if (isEmpty(source) || predicate == null) {
      return Collections.emptyList();
    }
    List<E> result = new ArrayList<>();
    for (E element : source) {
      if (predicate.test(element)) {
        result.add(element);
      }
    }
    return result;
  }

  public static <F, T> List<T> mapNotNull(Collection<F> source, Function<F, T> func) {
    return filter(map(source, func), Objects::nonNull);
  }

  public static <E> E findFirst(Collection<E> source) {
    return findFirst(source, e -> true);
  }

  public static <E> E findLast(Collection<E> source) {
    if (isEmpty(source)) {
      return null;
    }
    if (source.size() == 1) {
      return source.stream().findFirst().get();
    }
    // 逆序获取第一个
    return source.stream().sorted((x, y) -> -1).findFirst().get();
  }

  public static <E> E findFirst(Collection<E> elements, Predicate<E> predicate) {
    if (isEmpty(elements)) {
      return null;
    }
    for (E element : elements) {
      if (predicate.test(element)) {
        return element;
      }
    }
    return null;
  }

  /**
   * @param left            左集合
   * @param right           右集合(如果后面构造的key相同，后面的元素覆盖前面的，与sql的leftJoin有些区别)
   * @param leftKeyBuilder  左集合元素构造key的方法
   * @param rightKeyBuilder 右集合元素构造key的方法
   * @param resultBuilder   返回集合元素构造的方式
   * @return 连接结果
   */
  public static <F1, F2, K, R> List<R> leftJoin(Collection<F1> left, Collection<F2> right, Function<F1, K> leftKeyBuilder, Function<F2, K> rightKeyBuilder, BiFunction<F1, F2, R> resultBuilder) {
    if (isEmpty(left)) {
      return Collections.emptyList();
    }
    Map<K, F2> rightMap = toMap(right, rightKeyBuilder);

    List<R> result = new ArrayList<>(left.size());
    for (F1 element : left) {
      F2 rightElement = rightMap.get(leftKeyBuilder.apply(element));
      result.add(resultBuilder.apply(element, rightElement));
    }
    return result;
  }

  /**
   * @param left            左集合
   * @param right           右集合(如果后面构造的key相同，后面的元素覆盖前面的，与sql的leftJoin有些区别)
   * @param leftKeyBuilder  左集合元素构造key的方法
   * @param rightKeyBuilder 右集合元素构造key的方法
   * @param resultBuilder   返回集合元素构造的方式
   * @return 连接结果
   */
  public static <F1, F2, K, R> List<R> innerJoin(Collection<F1> left, Collection<F2> right, Function<F1, K> leftKeyBuilder, Function<F2, K> rightKeyBuilder, BiFunction<F1, F2, R> resultBuilder) {
    if (isEmpty(left)) {
      return Collections.emptyList();
    }
    Map<K, F2> rightMap = toMap(right, rightKeyBuilder);

    List<R> result = new ArrayList<>();
    for (F1 element : left) {
      F2 rightElement = rightMap.get(leftKeyBuilder.apply(element));
      if (rightElement == null) {
        continue;
      }
      result.add(resultBuilder.apply(element, rightElement));
    }
    return result;
  }

  public static <K, E extends E1, E1> Map<K, E> toMap(Collection<E> elements, Function<E1, K> keyBuilder) {
    if (isEmpty(elements) || keyBuilder == null) {
      return Maps.newHashMap();
    }
    Map<K, E> result = new HashMap<>(elements.size());
    for (E element : elements) {
      result.put(keyBuilder.apply(element), element);
    }
    return result;
  }

  public static <K, E, V> Map<K, V> toMap(Collection<E> elements, Function<E, K> keyBuilder, Function<E, V> valueBuilder) {
    if (isEmpty(elements) || keyBuilder == null) {
      return Maps.newHashMap();
    }
    Map<K, V> result = new HashMap<>(elements.size());
    for (E element : elements) {
      result.put(keyBuilder.apply(element), valueBuilder.apply(element));
    }
    return result;
  }


  public static <K, E, V> Map<K, V> toReduceMap(Collection<E> elements, Function<E, K> keyBuilder, V initValue, BiFunction<V, E, V> reduceFunc) {
    if (isEmpty(elements) || keyBuilder == null) {
      return Maps.newHashMap();
    }
    Map<K, V> result = Maps.newHashMap();
    Map<K, List<E>> kListMap = toMapList(elements, keyBuilder);
    kListMap.forEach((k, es) -> result.put(k, reduce(es, initValue, reduceFunc)));
    return result;
  }

  public static <K, E> Map<K, E> flatMap(Collection<E> elements, Function<E, List<K>> spiltFunc) {
    if (isEmpty(elements) || spiltFunc == null) {
      return Maps.newHashMap();
    }
    Map<K, E> result = Maps.newHashMap();
    for (E element : elements) {
      List<K> keys = spiltFunc.apply(element);
      if (isEmpty(keys)) {
        continue;
      }
      for (K key : keys) {
        result.put(key, element);
      }
    }
    return result;
  }

  public static <E, K> Map<K, List<E>> toMapList(Collection<E> elements, Function<E, K> keyFunc) {
    return toMapList(elements, keyFunc, Function.identity());
  }

  public static <E, K, V> Map<K, List<V>> toMapList(Collection<E> elements, Function<E, K> keyFunc, Function<E, V> valueFunc) {
    if (isEmpty(elements) || keyFunc == null || valueFunc == null) {
      return new HashMap<>();
    }
    Map<K, List<V>> result = Maps.newHashMap();
    for (E element : elements) {
      K key = keyFunc.apply(element);
      List<V> values = result.computeIfAbsent(key, k -> new ArrayList<>());
      values.add(valueFunc.apply(element));
    }
    return result;
  }

  public static <E, K, R> Map<K, R> groupBy(Collection<E> elements, Function<E, K> keyFunc, BiFunction<K, List<E>, R> resultBuilder) {
    if (isEmpty(elements) || keyFunc == null || resultBuilder == null) {
      return Collections.emptyMap();
    }
    Map<K, List<E>> map = toMapList(elements, keyFunc, Function.identity());

    Map<K, R> resultMap = Maps.newHashMap();
    for (Map.Entry<K, List<E>> entry : map.entrySet()) {
      resultMap.put(entry.getKey(), resultBuilder.apply(entry.getKey(), entry.getValue()));
    }
    return resultMap;
  }

  public static <E, R> R reduce(Collection<E> elements, R initValue, BiFunction<R, E, R> reduceFunc) {
    if (isEmpty(elements)) {
      return initValue;
    }
    R result = initValue;
    for (E element : elements) {
      if (null != element) {
        result = reduceFunc.apply(result, element);
      }
    }
    return result;
  }

  /***
   * 合并
   *
   * @author lvweijie
   * @date 2024/8/3 15:59
   */
  public static <E> List<E> combine(Collection<Collection<E>> es) {
    if (isEmpty(es)) {
      return new ArrayList<>();
    }
    List<E> result = new ArrayList<>();
    for (Collection<E> e : es) {
      if (isEmpty(e)) {
        continue;
      }
      result.addAll(e);
    }
    return result;
  }

  /***
   * 合并
   *
   * @author lvweijie
   * @date 2024/8/3 15:59
   */
  public static <E> List<E> combine(Collection<E> c, Collection<E>... other) {
    if (isEmpty(c) && null == other) {
      return new ArrayList<>();
    }
    List<E> result = isEmpty(c) ? new ArrayList<>() : new ArrayList<>(c);
    for (Collection<E> e : other) {
      if (isEmpty(e)) {
        continue;
      }
      result.addAll(e);
    }
    return result;
  }

  /***
   * 并集
   *
   * @author lvweijie
   * @date 2024/8/3 15:59
   */
  public static <E> List<E> union(Collection<Collection<E>> es) {
    if (isEmpty(es)) {
      return new ArrayList<>();
    }
    Set<E> set = new HashSet<>();
    for (Collection<E> e : es) {
      if (isEmpty(e)) {
        continue;
      }
      set.addAll(e);
    }
    return isEmpty(set) ? new ArrayList<>() : new ArrayList<>(set);
  }

  /***
   * 并集
   *
   * @author lvweijie
   * @date 2024/8/3 15:59
   */
  public static <E> List<E> union(Collection<E> c, Collection<E>... other) {
    if (isEmpty(c) && null == other) {
      return new ArrayList<>();
    }
    Set<E> set = isEmpty(c) ? new HashSet<>() : new HashSet<>(c);
    if (null != other) {
      for (Collection<E> e : other) {
        if (isEmpty(e)) {
          continue;
        }
        set.addAll(e);
      }
    }
    return isEmpty(set) ? new ArrayList<>() : new ArrayList<>(set);
  }

  /**
   * 并集
   *
   * @author lvweijie
   * @date 2024/8/7 21:48
   */
  public static <E> Map<String, E> union(Map<String, E> c, Map<String, E>... other) {
    if (isEmpty(c) && null == other) {
      return new HashMap<>();
    }
    Map<String, E> result = isNotEmpty(c) ? new HashMap<>(c) : new HashMap<>();
    if (null != other) {
      for (Map<String, E> e : other) {
        if (isEmpty(e)) {
          continue;
        }
        result.putAll(e);
      }
    }
    return result;
  }

  public static <T extends Object & Comparable<? super T>> T max(List<T> elements) {
    if (isEmpty(elements)) {
      return null;
    }
    return Collections.max(elements);
  }

  /**
   * 过滤重复出现的元素
   *
   * @param elements
   * @param keyFunc
   * @param <E>
   * @param <K>
   * @return
   */
  public static <E, K> List<E> filterSame(Collection<E> elements, Function<E, K> keyFunc) {
    Map<K, E> keMap = toMap(elements, keyFunc, Function.identity());
    return Lists.newArrayList(keMap.values());
  }

  public static <E> int size(List<E> elements) {
    if (isEmpty(elements)) {
      return 0;
    }
    return elements.size();
  }

  public static <E, K> boolean hasDistinct(Collection<E> elements, Function<E, K> keyFunc) {
    if (isEmpty(elements)) {
      return true;
    }
    long count = elements.stream().map(keyFunc).distinct().count();
    return count != elements.size();
  }

  public static <T> List<T> toList(Set<T> set) {
    return new ArrayList<>(set);
  }
}
