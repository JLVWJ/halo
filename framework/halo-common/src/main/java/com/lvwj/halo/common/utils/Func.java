package com.lvwj.halo.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Supplier;

/**
 * 工具包集合，工具类快捷方式
 */
@Slf4j
public class Func {

  public static <T> T getOrDefault(T value, T defaultValue) {
    return value != null ? value : defaultValue;
  }

  public static <T> T getOrDefault(T value, Supplier<T> defaultValueSupplier) {
    return value != null ? value : defaultValueSupplier.get();
  }

  /**
   * 断言，必须不能为 null
   * <blockquote><pre>
   * public Foo(Bar bar) {
   *     this.bar = $.requireNotNull(bar);
   * }
   * </pre></blockquote>
   *
   * @param obj the object reference to check for nullity
   * @param <T> the type of the reference
   * @return {@code obj} if not {@code null}
   * @throws NullPointerException if {@code obj} is {@code null}
   */
  public static <T> T requireNotNull(T obj) {
    return Objects.requireNonNull(obj);
  }

  /**
   * 断言，必须不能为 null
   * <blockquote><pre>
   * public Foo(Bar bar, Baz baz) {
   *     this.bar = $.requireNotNull(bar, "bar must not be null");
   *     this.baz = $.requireNotNull(baz, "baz must not be null");
   * }
   * </pre></blockquote>
   *
   * @param obj     the object reference to check for nullity
   * @param message detail message to be used in the event that a {@code NullPointerException} is thrown
   * @param <T>     the type of the reference
   * @return {@code obj} if not {@code null}
   * @throws NullPointerException if {@code obj} is {@code null}
   */
  public static <T> T requireNotNull(T obj, String message) {
    return Objects.requireNonNull(obj, message);
  }

  /**
   * 断言，必须不能为 null
   * <blockquote><pre>
   * public Foo(Bar bar, Baz baz) {
   *     this.bar = $.requireNotNull(bar, () -> "bar must not be null");
   * }
   * </pre></blockquote>
   *
   * @param obj             the object reference to check for nullity
   * @param messageSupplier supplier of the detail message to be used in the event that a {@code NullPointerException}
   *                        is thrown
   * @param <T>             the type of the reference
   * @return {@code obj} if not {@code null}
   * @throws NullPointerException if {@code obj} is {@code null}
   */
  public static <T> T requireNotNull(T obj, Supplier<String> messageSupplier) {
    return Objects.requireNonNull(obj, messageSupplier);
  }

  /**
   * 判断对象是否为null
   * <p>
   * This method exists to be used as a {@link java.util.function.Predicate}, {@code filter($::isNull)}
   * </p>
   *
   * @param obj a reference to be checked against {@code null}
   * @return {@code true} if the provided reference is {@code null} otherwise {@code false}
   * @see java.util.function.Predicate
   */
  public static boolean isNull(@Nullable Object obj) {
    return Objects.isNull(obj);
  }

  /**
   * 判断对象是否 not null
   * <p>
   * This method exists to be used as a {@link java.util.function.Predicate}, {@code filter($::notNull)}
   * </p>
   *
   * @param obj a reference to be checked against {@code null}
   * @return {@code true} if the provided reference is non-{@code null} otherwise {@code false}
   * @see java.util.function.Predicate
   */
  public static boolean notNull(@Nullable Object obj) {
    return Objects.nonNull(obj);
  }

  /**
   * 首字母变小写
   *
   * @param str 字符串
   * @return {String}
   */
  public static String firstCharToLower(String str) {
    return StringUtil.firstCharToLower(str);
  }

  /**
   * 首字母变大写
   *
   * @param str 字符串
   * @return {String}
   */
  public static String firstCharToUpper(String str) {
    return StringUtil.firstCharToUpper(str);
  }

  /**
   * 判断是否为空字符串
   * <pre class="code">
   * $.isBlank(null)		= true
   * $.isBlank("")		= true
   * $.isBlank(" ")		= true
   * $.isBlank("12345")	= false
   * $.isBlank(" 12345 ")	= false
   * </pre>
   */
  public static boolean isBlank(@Nullable final CharSequence cs) {
    return StringUtil.isBlank(cs);
  }

  /**
   * 判断不为空字符串
   * <pre>
   * $.isNotBlank(null)	= false
   * $.isNotBlank("")		= false
   * $.isNotBlank(" ")	= false
   * $.isNotBlank("bob")	= true
   * $.isNotBlank("  bob  ") = true
   * </pre>
   */
  public static boolean isNotBlank(@Nullable final CharSequence cs) {
    return StringUtil.isNotBlank(cs);
  }

  /**
   * 判断是否有任意一个空字符串
   *
   * @param css CharSequence
   * @return boolean
   */
  public static boolean isAnyBlank(final CharSequence... css) {
    return StringUtil.isAnyBlank(css);
  }

  /**
   * 判断是否全为非空字符串
   *
   * @param css CharSequence
   * @return boolean
   */
  public static boolean isNoneBlank(final CharSequence... css) {
    return StringUtil.isNoneBlank(css);
  }

  /**
   * 判断对象是数组
   *
   * @param obj the object to check
   * @return 是否数组
   */
  public static boolean isArray(@Nullable Object obj) {
    return ObjectUtil.isArray(obj);
  }

  /**
   * 判断空对象 object、map、list、set、字符串、数组
   *
   * @param obj the object to check
   * @return 数组是否为空
   */
  public static boolean isEmpty(@Nullable Object obj) {
    return ObjectUtil.isEmpty(obj);
  }

  /**
   * 对象不为空 object、map、list、set、字符串、数组
   *
   * @param obj the object to check
   * @return 是否不为空
   */
  public static boolean isNotEmpty(@Nullable Object obj) {
    return !ObjectUtil.isEmpty(obj);
  }

  /**
   * 判断数组为空
   *
   * @param array the array to check
   * @return 数组是否为空
   */
  public static boolean isEmpty(@Nullable Object[] array) {
    return ObjectUtil.isEmpty(array);
  }

  /**
   * 判断数组不为空
   *
   * @param array 数组
   * @return 数组是否不为空
   */
  public static boolean isNotEmpty(@Nullable Object[] array) {
    return ObjectUtil.isNotEmpty(array);
  }

  /**
   * 对象组中是否存在 Empty Object
   *
   * @param os 对象组
   * @return boolean
   */
  public static boolean hasEmpty(Object... os) {
    for (Object o : os) {
      if (isEmpty(o)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 对象组中是否全部为 Empty Object
   *
   * @param os 对象组
   * @return boolean
   */
  public static boolean isAllEmpty(Object... os) {
    for (Object o : os) {
      if (isNotEmpty(o)) {
        return false;
      }
    }
    return true;
  }

  /**
   * 将字符串中特定模式的字符转换成map中对应的值
   * <p>
   * use: format("my name is ${name}, and i like ${like}!", {"name":"L.cm", "like": "Java"})
   *
   * @param message 需要转换的字符串
   * @param params  转换所需的键值对集合
   * @return 转换后的字符串
   */
  public static String format(@Nullable String message, @Nullable Map<String, Object> params) {
    return StringUtil.format(message, params);
  }

  /**
   * 同 log 格式的 format 规则
   * <p>
   * use: format("my name is {}, and i like {}!", "L.cm", "Java")
   *
   * @param message   需要转换的字符串
   * @param arguments 需要替换的变量
   * @return 转换后的字符串
   */
  public static String format(@Nullable String message, @Nullable Object... arguments) {
    return StringUtil.format(message, arguments);
  }

  public static String removeLineBreak(String value) {
    if (value == null) {
      return null;
    }
    return value.replaceAll("[\r|\n]", "");
  }

  /**
   * 比较两个对象是否相等。<br> 相同的条件有两个，满足其一即可：<br>
   *
   * @param obj1 对象1
   * @param obj2 对象2
   * @return 是否相等
   */
  public static boolean equals(Object obj1, Object obj2) {
    return Objects.equals(obj1, obj2);
  }

  /**
   * 安全的 equals
   *
   * @param o1 first Object to compare
   * @param o2 second Object to compare
   * @return whether the given objects are equal
   * @see Object#equals(Object)
   * @see Arrays#equals
   */
  public static boolean equalsSafe(@Nullable Object o1, @Nullable Object o2) {
    return ObjectUtil.nullSafeEquals(o1, o2);
  }

  /**
   * 判断数组中是否包含元素
   *
   * @param array   the Array to check
   * @param element the element to look for
   * @param <T>     The generic tag
   * @return {@code true} if found, {@code false} else
   */
  public static <T> boolean contains(@Nullable T[] array, final T element) {
    return CollectionUtil.contains(array, element);
  }

  /**
   * 判断迭代器中是否包含元素
   *
   * @param iterator the Iterator to check
   * @param element  the element to look for
   * @return {@code true} if found, {@code false} otherwise
   */
  public static boolean contains(@Nullable Iterator<?> iterator, Object element) {
    return CollectionUtil.contains(iterator, element);
  }

  /**
   * 判断枚举是否包含该元素
   *
   * @param enumeration the Enumeration to check
   * @param element     the element to look for
   * @return {@code true} if found, {@code false} otherwise
   */
  public static boolean contains(@Nullable Enumeration<?> enumeration, Object element) {
    return CollectionUtil.contains(enumeration, element);
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
    return CollectionUtil.ofImmutableSet(es);
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
    return CollectionUtil.ofImmutableList(es);
  }

  /**
   * 强转string,并去掉多余空格
   *
   * @param str 字符串
   * @return {String}
   */
  public static String toStr(Object str) {
    return toStr(str, "");
  }

  /**
   * 强转string,并去掉多余空格
   *
   * @param str          字符串
   * @param defaultValue 默认值
   * @return {String}
   */
  public static String toStr(Object str, String defaultValue) {
    if (null == str || str.equals(StringPool.NULL)) {
      return defaultValue;
    }
    return String.valueOf(str);
  }

  /**
   * 强转string(包含空字符串),并去掉多余空格
   *
   * @param str          字符串
   * @param defaultValue 默认值
   * @return {String}
   */
  public static String toStrWithEmpty(Object str, String defaultValue) {
    if (null == str || str.equals(StringPool.NULL) || str.equals(StringPool.EMPTY)) {
      return defaultValue;
    }
    return String.valueOf(str);
  }


  /**
   * 判断一个字符串是否是数字
   *
   * @param cs the CharSequence to check, may be null
   * @return {boolean}
   */
  public static boolean isNumeric(final CharSequence cs) {
    return StringUtil.isNumeric(cs);
  }

  /**
   * 字符串转Integer，为空则返回null
   */
  public static Integer toInt(final Object str) {
    return toInt(str, null);
  }

  /**
   * 字符串转Integer，为空则返回默认值
   */
  public static Integer toInt(@Nullable final Object str, final Integer defaultValue) {
    return NumberUtil.toInt(String.valueOf(str), defaultValue);
  }

  /**
   * 字符串转Long，为空则返回null
   */
  public static Long toLong(final Object str) {
    return toLong(str, null);
  }

  /**
   * 字符串转Long，为空则返回默认值
   */
  public static Long toLong(@Nullable final Object str, final Long defaultValue) {
    return NumberUtil.toLong(String.valueOf(str), defaultValue);
  }

  /**
   * 字符串转Double，为空则返回null
   */
  public static Double toDouble(Object value) {
    return toDouble(value, null);
  }

  /**
   * 字符串转Double，为空则返回默认值
   */
  public static Double toDouble(Object value, Double defaultValue) {
    return NumberUtil.toDouble(String.valueOf(value), defaultValue);
  }

  /**
   * 字符串转Float，为空则返回null
   */
  public static Float toFloat(Object value) {
    return toFloat(value, null);
  }

  /**
   * 字符串转Float，为空则返回默认值
   */
  public static Float toFloat(Object value, Float defaultValue) {
    return NumberUtil.toFloat(String.valueOf(value), defaultValue);
  }

  /**
   * 字符串转Boolean，为空则返回null
   */
  public static Boolean toBoolean(Object value) {
    return toBoolean(value, null);
  }

  /**
   * 字符串转Boolean，为空则返回默认值
   */
  public static Boolean toBoolean(Object value, Boolean defaultValue) {
    if (value != null) {
      return Boolean.parseBoolean(String.valueOf(value).trim());
    }
    return defaultValue;
  }

  /**
   * 转换为Integer数组<br>
   *
   * @param str 被转换的值
   * @return 结果
   */
  public static Integer[] toIntArray(String str) {
    return toIntArray(",", str);
  }

  /**
   * 转换为Integer数组<br>
   *
   * @param split 分隔符
   * @param str   被转换的值
   * @return 结果
   */
  public static Integer[] toIntArray(String split, String str) {
    if (StringUtil.isBlank(str)) {
      return new Integer[0];
    }
    String[] arr = str.split(split);
    final Integer[] ints = new Integer[arr.length];
    for (int i = 0; i < arr.length; i++) {
      ints[i] = toInt(arr[i]);
    }
    return ints;
  }

  /**
   * 转换为Integer集合<br>
   *
   * @param str 结果被转换的值
   * @return 结果
   */
  public static List<Integer> toIntList(String str) {
    return Arrays.asList(toIntArray(str));
  }

  /**
   * 转换为Integer集合<br>
   *
   * @param split 分隔符
   * @param str   被转换的值
   * @return 结果
   */
  public static List<Integer> toIntList(String split, String str) {
    return Arrays.asList(toIntArray(split, str));
  }

  /**
   * 获取第一位Integer数值
   *
   * @param str 被转换的值
   * @return 结果
   */
  public static Integer firstInt(String str) {
    return firstInt(",", str);
  }

  /**
   * 获取第一位Integer数值
   *
   * @param split 分隔符
   * @param str   被转换的值
   * @return 结果
   */
  public static Integer firstInt(String split, String str) {
    List<Integer> ints = toIntList(split, str);
    if (isEmpty(ints)) {
      return null;
    } else {
      return ints.get(0);
    }
  }

  /**
   * 转换为Long数组<br>
   *
   * @param str 被转换的值
   * @return 结果
   */
  public static Long[] toLongArray(String str) {
    return toLongArray(",", str);
  }

  /**
   * 转换为Long数组<br>
   *
   * @param split 分隔符
   * @param str   被转换的值
   * @return 结果
   */
  public static Long[] toLongArray(String split, String str) {
    if (StringUtil.isBlank(str)) {
      return new Long[]{};
    }
    String[] arr = str.split(split);
    final Long[] longs = new Long[arr.length];
    for (int i = 0; i < arr.length; i++) {
      longs[i] = toLong(arr[i]);
    }
    return longs;
  }

  /**
   * 转换为Long集合<br>
   *
   * @param str 结果被转换的值
   * @return 结果
   */
  public static List<Long> toLongList(String str) {
    return Arrays.asList(toLongArray(str));
  }

  /**
   * 转换为Long集合<br>
   *
   * @param split 分隔符
   * @param str   被转换的值
   * @return 结果
   */
  public static List<Long> toLongList(String split, String str) {
    return Arrays.asList(toLongArray(split, str));
  }

  /**
   * 获取第一位Long数值
   *
   * @param str 被转换的值
   * @return 结果
   */
  public static Long firstLong(String str) {
    return firstLong(",", str);
  }

  /**
   * 获取第一位Long数值
   *
   * @param split 分隔符
   * @param str   被转换的值
   * @return 结果
   */
  public static Long firstLong(String split, String str) {
    List<Long> longs = toLongList(split, str);
    if (isEmpty(longs)) {
      return null;
    } else {
      return longs.get(0);
    }
  }

  /**
   * 转换为String数组<br>
   *
   * @param str 被转换的值
   * @return 结果
   */
  public static String[] toStrArray(String str) {
    return toStrArray(",", str);
  }

  /**
   * 转换为String数组<br>
   *
   * @param split 分隔符
   * @param str   被转换的值
   * @return 结果
   */
  public static String[] toStrArray(String split, String str) {
    if (isBlank(str)) {
      return new String[0];
    }
    return str.split(split);
  }

  /**
   * 转换为String集合<br>
   *
   * @param str 结果被转换的值
   * @return 结果
   */
  public static List<String> toStrList(String str) {
    return Arrays.asList(toStrArray(str));
  }

  /**
   * 转换为String集合<br>
   *
   * @param split 分隔符
   * @param str   被转换的值
   * @return 结果
   */
  public static List<String> toStrList(String split, String str) {
    return Arrays.asList(toStrArray(split, str));
  }

  /**
   * 获取第一位String数值
   *
   * @param str 被转换的值
   * @return 结果
   */
  public static String firstStr(String str) {
    return firstStr(",", str);
  }

  /**
   * 获取第一位String数值
   *
   * @param split 分隔符
   * @param str   被转换的值
   * @return 结果
   */
  public static String firstStr(String split, String str) {
    List<String> strList = toStrList(split, str);
    if (isEmpty(strList)) {
      return null;
    } else {
      return strList.get(0);
    }
  }

  /**
   * 将 long 转短字符串 为 62 进制
   *
   * @param num 数字
   * @return 短字符串
   */
  public static String to62String(long num) {
    return NumberUtil.to62String(num);
  }

  /**
   * 将集合拼接成字符串，默认使用`,`拼接
   *
   * @param coll the {@code Collection} to convert
   * @return the delimited {@code String}
   */
  public static String join(Collection<?> coll) {
    return StringUtil.join(coll);
  }

  /**
   * 将集合拼接成字符串，默认指定分隔符
   *
   * @param coll  the {@code Collection} to convert
   * @param delim the delimiter to use (typically a ",")
   * @return the delimited {@code String}
   */
  public static String join(Collection<?> coll, String delim) {
    return StringUtil.join(coll, delim);
  }

  /**
   * 将数组拼接成字符串，默认使用`,`拼接
   *
   * @param arr the array to display
   * @return the delimited {@code String}
   */
  public static String join(Object[] arr) {
    return StringUtil.join(arr);
  }

  /**
   * 将数组拼接成字符串，默认指定分隔符
   *
   * @param arr   the array to display
   * @param delim the delimiter to use (typically a ",")
   * @return the delimited {@code String}
   */
  public static String join(Object[] arr, String delim) {
    return StringUtil.join(arr, delim);
  }

  /**
   * 切分字符串，不去除切分后每个元素两边的空白符，不去除空白项
   *
   * @param str       被切分的字符串
   * @param separator 分隔符字符
   * @return 切分后的集合
   */
  public static List<String> split(CharSequence str, char separator) {
    return StringUtil.split(str, separator, -1);
  }

  /**
   * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
   *
   * @param str       被切分的字符串
   * @param separator 分隔符字符
   * @return 切分后的集合
   */
  public static List<String> splitTrim(CharSequence str, char separator) {
    return StringUtil.splitTrim(str, separator);
  }

  /**
   * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
   *
   * @param str       被切分的字符串
   * @param separator 分隔符字符
   * @return 切分后的集合
   */
  public static List<String> splitTrim(CharSequence str, CharSequence separator) {
    return StringUtil.splitTrim(str, separator);
  }

  /**
   * 分割 字符串
   *
   * @param str       字符串
   * @param delimiter 分割符
   * @return 字符串数组
   */
  public static String[] split(@Nullable String str, @Nullable String delimiter) {
    return StringUtil.delimitedListToStringArray(str, delimiter);
  }

  /**
   * 分割 字符串 删除常见 空白符
   *
   * @param str       字符串
   * @param delimiter 分割符
   * @return 字符串数组
   */
  public static String[] splitTrim(@Nullable String str, @Nullable String delimiter) {
    return StringUtil.delimitedListToStringArray(str, delimiter, " \t\n\n\f");
  }

  /**
   * 字符串是否符合指定的 表达式
   *
   * <p>
   * pattern styles: "xxx*", "*xxx", "*xxx*" and "xxx*yyy"
   * </p>
   *
   * @param pattern 表达式
   * @param str     字符串
   * @return 是否匹配
   */
  public static boolean simpleMatch(@Nullable String pattern, @Nullable String str) {
    return PatternMatchUtils.simpleMatch(pattern, str);
  }

  /**
   * 字符串是否符合指定的 表达式
   *
   * <p>
   * pattern styles: "xxx*", "*xxx", "*xxx*" and "xxx*yyy"
   * </p>
   *
   * @param patterns 表达式 数组
   * @param str      字符串
   * @return 是否匹配
   */
  public static boolean simpleMatch(@Nullable String[] patterns, String str) {
    return PatternMatchUtils.simpleMatch(patterns, str);
  }

  /**
   * 生成uuid
   *
   * @return UUID
   */
  public static String randomUUID() {
    return UUID.randomUUID().toString();
  }

  /**
   * 随机数生成
   *
   * @param count 字符长度
   * @return 随机数
   */
  public static String random(int count) {
    return StringUtil.random(count);
  }

  /**
   * 随机数生成
   *
   * @param count      字符长度
   * @param randomType 随机数类别
   * @return 随机数
   */
  public static String random(int count, RandomType randomType) {
    return StringUtil.random(count, randomType);
  }

  /**
   * 字符串序列化成 md5
   *
   * @param data Data to digest
   * @return MD5 digest as a hex string
   */
  public static String md5Hex(final String data) {
    return DigestUtil.md5Hex(data);
  }

  /**
   * 数组序列化成 md5
   *
   * @param bytes the bytes to calculate the digest over
   * @return md5 digest string
   */
  public static String md5Hex(final byte[] bytes) {
    return DigestUtil.md5Hex(bytes);
  }


  /**
   * sha1Hex
   *
   * @param data Data to digest
   * @return digest as a hex string
   */
  public static String sha1Hex(String data) {
    return DigestUtil.sha1Hex(data);
  }

  /**
   * sha1Hex
   *
   * @param bytes Data to digest
   * @return digest as a hex string
   */
  public static String sha1Hex(final byte[] bytes) {
    return DigestUtil.sha1Hex(bytes);
  }

  /**
   * SHA224Hex
   *
   * @param data Data to digest
   * @return digest as a hex string
   */
  public static String sha224Hex(String data) {
    return DigestUtil.sha224Hex(data);
  }

  /**
   * SHA224Hex
   *
   * @param bytes Data to digest
   * @return digest as a hex string
   */
  public static String sha224Hex(final byte[] bytes) {
    return DigestUtil.sha224Hex(bytes);
  }

  /**
   * sha256Hex
   *
   * @param data Data to digest
   * @return digest as a hex string
   */
  public static String sha256Hex(String data) {
    return DigestUtil.sha256Hex(data);
  }

  /**
   * sha256Hex
   *
   * @param bytes Data to digest
   * @return digest as a hex string
   */
  public static String sha256Hex(final byte[] bytes) {
    return DigestUtil.sha256Hex(bytes);
  }

  /**
   * sha384Hex
   *
   * @param data Data to digest
   * @return digest as a hex string
   */
  public static String sha384Hex(String data) {
    return DigestUtil.sha384Hex(data);
  }

  /**
   * sha384Hex
   *
   * @param bytes Data to digest
   * @return digest as a hex string
   */
  public static String sha384Hex(final byte[] bytes) {
    return DigestUtil.sha384Hex(bytes);
  }

  /**
   * sha512Hex
   *
   * @param data Data to digest
   * @return digest as a hex string
   */
  public static String sha512Hex(String data) {
    return DigestUtil.sha512Hex(data);
  }

  /**
   * sha512Hex
   *
   * @param bytes Data to digest
   * @return digest as a hex string
   */
  public static String sha512Hex(final byte[] bytes) {
    return DigestUtil.sha512Hex(bytes);
  }

  /**
   * hmacMd5 Hex
   *
   * @param data Data to digest
   * @param key  key
   * @return digest as a hex string
   */
  public static String hmacMd5Hex(String data, String key) {
    return DigestUtil.hmacMd5Hex(data, key);
  }

  /**
   * hmacMd5 Hex
   *
   * @param bytes Data to digest
   * @param key   key
   * @return digest as a hex string
   */
  public static String hmacMd5Hex(final byte[] bytes, String key) {
    return DigestUtil.hmacMd5Hex(bytes, key);
  }

  /**
   * hmacSha1 Hex
   *
   * @param data Data to digest
   * @param key  key
   * @return digest as a hex string
   */
  public static String hmacSha1Hex(String data, String key) {
    return DigestUtil.hmacSha1Hex(data, key);
  }

  /**
   * hmacSha1 Hex
   *
   * @param bytes Data to digest
   * @param key   key
   * @return digest as a hex string
   */
  public static String hmacSha1Hex(final byte[] bytes, String key) {
    return DigestUtil.hmacSha1Hex(bytes, key);
  }

  /**
   * hmacSha224 Hex
   *
   * @param data Data to digest
   * @param key  key
   * @return digest as a hex string
   */
  public static String hmacSha224Hex(String data, String key) {
    return DigestUtil.hmacSha224Hex(data, key);
  }

  /**
   * hmacSha224 Hex
   *
   * @param bytes Data to digest
   * @param key   key
   * @return digest as a hex string
   */
  public static String hmacSha224Hex(final byte[] bytes, String key) {
    return DigestUtil.hmacSha224Hex(bytes, key);
  }

  /**
   * hmacSha256 Hex
   *
   * @param data Data to digest
   * @param key  key
   * @return digest as a hex string
   */
  public static String hmacSha256Hex(String data, String key) {
    return DigestUtil.hmacSha256Hex(data, key);
  }

  /**
   * hmacSha256 Hex
   *
   * @param bytes Data to digest
   * @param key   key
   * @return digest as a hex string
   */
  public static String hmacSha256Hex(final byte[] bytes, String key) {
    return DigestUtil.hmacSha256Hex(bytes, key);
  }

  /**
   * hmacSha384 Hex
   *
   * @param data Data to digest
   * @param key  key
   * @return digest as a hex string
   */
  public static String hmacSha384Hex(String data, String key) {
    return DigestUtil.hmacSha384Hex(data, key);
  }

  /**
   * hmacSha384 Hex
   *
   * @param bytes Data to digest
   * @param key   key
   * @return digest as a hex string
   */
  public static String hmacSha384Hex(final byte[] bytes, String key) {
    return DigestUtil.hmacSha384Hex(bytes, key);
  }

  /**
   * hmacSha512 Hex
   *
   * @param data Data to digest
   * @param key  key
   * @return digest as a hex string
   */
  public static String hmacSha512Hex(String data, String key) {
    return DigestUtil.hmacSha512Hex(data, key);
  }

  /**
   * hmacSha512 Hex
   *
   * @param bytes Data to digest
   * @param key   key
   * @return digest as a hex string
   */
  public static String hmacSha512Hex(final byte[] bytes, String key) {
    return DigestUtil.hmacSha512Hex(bytes, key);
  }

  /**
   * byte 数组序列化成 hex
   *
   * @param bytes bytes to encode
   * @return MD5 digest as a hex string
   */
  public static String encodeHex(byte[] bytes) {
    return DigestUtil.encodeHex(bytes);
  }

  /**
   * 字符串反序列化成 hex
   *
   * @param hexString String to decode
   * @return MD5 digest as a hex string
   */
  public static byte[] decodeHex(final String hexString) {
    return DigestUtil.decodeHex(hexString);
  }

  /**
   * Base64编码
   *
   * @param value 字符串
   * @return {String}
   */
  public static String encodeBase64(String value) {
    return Base64Util.encode(value);
  }

  /**
   * Base64编码
   *
   * @param value   字符串
   * @param charset 字符集
   * @return {String}
   */
  public static String encodeBase64(String value, Charset charset) {
    return Base64Util.encode(value, charset);
  }

  /**
   * Base64编码为URL安全
   *
   * @param value 字符串
   * @return {String}
   */
  public static String encodeBase64UrlSafe(String value) {
    return Base64Util.encodeUrlSafe(value);
  }

  /**
   * Base64编码为URL安全
   *
   * @param value   字符串
   * @param charset 字符集
   * @return {String}
   */
  public static String encodeBase64UrlSafe(String value, Charset charset) {
    return Base64Util.encodeUrlSafe(value, charset);
  }

  /**
   * Base64解码
   *
   * @param value 字符串
   * @return {String}
   */
  public static String decodeBase64(String value) {
    return Base64Util.decode(value);
  }

  /**
   * Base64解码
   *
   * @param value   字符串
   * @param charset 字符集
   * @return {String}
   */
  public static String decodeBase64(String value, Charset charset) {
    return Base64Util.decode(value, charset);
  }

  /**
   * Base64URL安全解码
   *
   * @param value 字符串
   * @return {String}
   */
  public static String decodeBase64UrlSafe(String value) {
    return Base64Util.decodeUrlSafe(value);
  }

  /**
   * Base64URL安全解码
   *
   * @param value   字符串
   * @param charset 字符集
   * @return {String}
   */
  public static String decodeBase64UrlSafe(String value, Charset charset) {
    return Base64Util.decodeUrlSafe(value, charset);
  }

  /**
   * 关闭 Closeable
   *
   * @param closeable 自动关闭
   */
  public static void closeQuietly(@Nullable Closeable closeable) {
    IOUtil.closeQuietly(closeable);
  }

  /**
   * InputStream to String utf-8
   *
   * @param input the <code>InputStream</code> to read from
   * @return the requested String
   * @throws NullPointerException if the input is null
   */
  public static String readToString(InputStream input) {
    return IOUtil.readToString(input);
  }

  /**
   * InputStream to String
   *
   * @param input   the <code>InputStream</code> to read from
   * @param charset the <code>Charset</code>
   * @return the requested String
   * @throws NullPointerException if the input is null
   */
  public static String readToString(@Nullable InputStream input, Charset charset) {
    return IOUtil.readToString(input, charset);
  }

  /**
   * InputStream to bytes 数组
   *
   * @param input InputStream
   * @return the requested byte array
   */
  public static byte[] readToByteArray(@Nullable InputStream input) {
    return IOUtil.readToByteArray(input);
  }

  /**
   * 读取文件为字符串
   *
   * @param file the file to read, must not be {@code null}
   * @return the file contents, never {@code null}
   */
  public static String readToString(final File file) {
    return FileUtil.readToString(file);
  }

  /**
   * 读取文件为字符串
   *
   * @param file     the file to read, must not be {@code null}
   * @param encoding the encoding to use, {@code null} means platform default
   * @return the file contents, never {@code null}
   */
  public static String readToString(File file, Charset encoding) {
    return FileUtil.readToString(file, encoding);
  }

  /**
   * 读取文件为 byte 数组
   *
   * @param file the file to read, must not be {@code null}
   * @return the file contents, never {@code null}
   */
  public static byte[] readToByteArray(File file) {
    return FileUtil.readToByteArray(file);
  }

  /**
   * 将对象序列化成json字符串
   *
   * @param object javaBean
   * @return jsonString json字符串
   */
  public static String toJson(Object object) {
    return JsonUtil.toJson(object);
  }

  /**
   * 将对象序列化成 json byte 数组
   *
   * @param object javaBean
   * @return jsonString json字符串
   */
  public static byte[] toJsonAsBytes(Object object) {
    return JsonUtil.toJsonAsBytes(object);
  }

  /**
   * 将json字符串转成 JsonNode
   *
   * @param jsonString jsonString
   * @return jsonString json字符串
   */
  public static JsonNode readTree(String jsonString) {
    return JsonUtil.readTree(jsonString);
  }

  /**
   * 将json字符串转成 JsonNode
   *
   * @param in InputStream
   * @return jsonString json字符串
   */
  public static JsonNode readTree(InputStream in) {
    return JsonUtil.readTree(in);
  }

  /**
   * 将json字符串转成 JsonNode
   *
   * @param content content
   * @return jsonString json字符串
   */
  public static JsonNode readTree(byte[] content) {
    return JsonUtil.readTree(content);
  }

  /**
   * 将json字符串转成 JsonNode
   *
   * @param jsonParser JsonParser
   * @return jsonString json字符串
   */
  public static JsonNode readTree(JsonParser jsonParser) {
    return JsonUtil.readTree(jsonParser);
  }

  /**
   * 将json byte 数组反序列化成对象
   *
   * @param bytes     json bytes
   * @param valueType class
   * @param <T>       T 泛型标记
   * @return Bean
   */
  public static <T> T readJson(byte[] bytes, Class<T> valueType) {
    return JsonUtil.parse(bytes, valueType);
  }

  /**
   * 将json反序列化成对象
   *
   * @param jsonString jsonString
   * @param valueType  class
   * @param <T>        T 泛型标记
   * @return Bean
   */
  public static <T> T readJson(String jsonString, Class<T> valueType) {
    return JsonUtil.parse(jsonString, valueType);
  }

  /**
   * 将json反序列化成对象
   *
   * @param in        InputStream
   * @param valueType class
   * @param <T>       T 泛型标记
   * @return Bean
   */
  public static <T> T readJson(InputStream in, Class<T> valueType) {
    return JsonUtil.parse(in, valueType);
  }

  /**
   * 将json反序列化成对象
   *
   * @param bytes         bytes
   * @param typeReference 泛型类型
   * @param <T>           T 泛型标记
   * @return Bean
   */
  public static <T> T readJson(byte[] bytes, TypeReference<T> typeReference) {
    return JsonUtil.parse(bytes, typeReference);
  }

  /**
   * 将json反序列化成对象
   *
   * @param jsonString    jsonString
   * @param typeReference 泛型类型
   * @param <T>           T 泛型标记
   * @return Bean
   */
  public static <T> T readJson(String jsonString, TypeReference<T> typeReference) {
    return JsonUtil.parse(jsonString, typeReference);
  }

  /**
   * 将json反序列化成对象
   *
   * @param in            InputStream
   * @param typeReference 泛型类型
   * @param <T>           T 泛型标记
   * @return Bean
   */
  public static <T> T readJson(InputStream in, TypeReference<T> typeReference) {
    return JsonUtil.parse(in, typeReference);
  }

  /**
   * url 解码
   *
   * @param source the encoded String
   * @return the decoded value
   * @throws IllegalArgumentException when the given source contains invalid encoded sequences
   * @see StringUtils#uriDecode(String, Charset)
   * @see java.net.URLDecoder#decode(String, String)
   */
  public static String urlDecode(String source) {
    return StringUtil.uriDecode(source, StandardCharsets.UTF_8);
  }

  /**
   * url 解码
   *
   * @param source  the encoded String
   * @param charset the character encoding to use
   * @return the decoded value
   * @throws IllegalArgumentException when the given source contains invalid encoded sequences
   * @see StringUtils#uriDecode(String, Charset)
   * @see java.net.URLDecoder#decode(String, String)
   */
  public static String urlDecode(String source, Charset charset) {
    return StringUtil.uriDecode(source, charset);
  }

  public static String formatDateTimeS(TemporalAccessor temporal) {
    return DateTimeUtil.formatDateTimeS(temporal);
  }

  public static String formatDateTimeM(TemporalAccessor temporal) {
    return DateTimeUtil.formatDateTimeM(temporal);
  }

  /**
   * 日期时间格式化
   *
   * @param temporal 时间
   * @return 格式化后的时间
   */
  public static String formatDateTime(TemporalAccessor temporal) {
    return DateTimeUtil.formatDateTime(temporal);
  }

  /**
   * 日期时间格式化
   *
   * @param temporal 时间
   * @return 格式化后的时间
   */
  public static String formatDate(TemporalAccessor temporal) {
    return DateTimeUtil.formatDate(temporal);
  }

  /**
   * 时间格式化
   *
   * @param temporal 时间
   * @return 格式化后的时间
   */
  public static String formatTime(TemporalAccessor temporal) {
    return DateTimeUtil.formatTime(temporal);
  }

  public static String format(TemporalAccessor temporal, String pattern) {
    return DateTimeUtil.format(temporal, pattern);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr   时间字符串
   * @param formatter DateTimeFormatter
   * @return 时间
   */
  public static LocalDateTime parseDateTime(String dateStr, DateTimeFormatter formatter) {
    return DateTimeUtil.parseDateTime(dateStr, formatter);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr 时间字符串
   * @return 时间
   */
  public static LocalDateTime parseDateTime(String dateStr) {
    return DateTimeUtil.parseDateTime(dateStr);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr   时间字符串
   * @param formatter DateTimeFormatter
   * @return 时间
   */
  public static LocalDate parseDate(String dateStr, DateTimeFormatter formatter) {
    return DateTimeUtil.parseDate(dateStr, formatter);
  }

  /**
   * 将字符串转换为日期
   *
   * @param dateStr 时间字符串
   * @return 时间
   */
  public static LocalDate parseDate(String dateStr) {
    return DateTimeUtil.parseDate(dateStr, DateTimeUtil.DATE_FORMAT);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr   时间字符串
   * @param formatter DateTimeFormatter
   * @return 时间
   */
  public static LocalTime parseTime(String dateStr, DateTimeFormatter formatter) {
    return DateTimeUtil.parseTime(dateStr, formatter);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr 时间字符串
   * @return 时间
   */
  public static LocalTime parseTime(String dateStr) {
    return DateTimeUtil.parseTime(dateStr);
  }

  /**
   * 时间比较
   *
   * @param startInclusive the start instant, inclusive, not null
   * @param endExclusive   the end instant, exclusive, not null
   * @return a {@code Duration}, not null
   */
  public static Duration between(Temporal startInclusive, Temporal endExclusive) {
    return Duration.between(startInclusive, endExclusive);
  }

  /**
   * 获取方法参数信息
   *
   * @param constructor    构造器
   * @param parameterIndex 参数序号
   * @return {MethodParameter}
   */
  public static MethodParameter getMethodParameter(Constructor<?> constructor, int parameterIndex) {
    return ClassUtil.getMethodParameter(constructor, parameterIndex);
  }

  /**
   * 获取方法参数信息
   *
   * @param method         方法
   * @param parameterIndex 参数序号
   * @return {MethodParameter}
   */
  public static MethodParameter getMethodParameter(Method method, int parameterIndex) {
    return ClassUtil.getMethodParameter(method, parameterIndex);
  }

  /**
   * 获取Annotation注解
   *
   * @param annotatedElement AnnotatedElement
   * @param annotationType   注解类
   * @param <A>              泛型标记
   * @return {Annotation}
   */
  @Nullable
  public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
    return AnnotatedElementUtils.findMergedAnnotation(annotatedElement, annotationType);
  }

  /**
   * 获取Annotation，先找方法，没有则再找方法上的类
   *
   * @param method         Method
   * @param annotationType 注解类
   * @param <A>            泛型标记
   * @return {Annotation}
   */
  @Nullable
  public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
    return ClassUtil.getAnnotation(method, annotationType);
  }

  /**
   * 实例化对象
   *
   * @param clazz 类
   * @param <T>   泛型标记
   * @return 对象
   */
  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<?> clazz) {
    return (T) BeanUtils.instantiateClass(clazz);
  }

  /**
   * 实例化对象
   *
   * @param clazzStr 类名
   * @param <T>      泛型标记
   * @return 对象
   */
  public static <T> T newInstance(String clazzStr) {
    try {
      Class<?> clazz = ClassUtil.forName(clazzStr, null);
      return newInstance(clazz);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 获取Bean的属性
   *
   * @param bean         bean
   * @param propertyName 属性名
   * @return 属性值
   */
  @Nullable
  public static Object getProperty(@Nullable Object bean, String propertyName) {
    return BeanUtil.getProperty(bean, propertyName);
  }

  /**
   * 设置Bean属性
   *
   * @param bean         bean
   * @param propertyName 属性名
   * @param value        属性值
   */
  public static void setProperty(Object bean, String propertyName, Object value) {
    BeanUtil.setProperty(bean, propertyName, value);
  }

  /**
   * 基于json的数据转换实体，支持json的所有注解 属性不存在 不会抛出异常
   *
   * @param source 源对象
   * @param clazz  目标类型
   * @param <T>    泛型标记
   * @return T
   */
  public static <T> T copyWithJson(@Nullable Object source, Class<T> clazz) {
    return JsonUtil.parse(toJson(source), clazz);
  }

  /**
   * 基于json的数据转换 list  支持各种实体,同时支持 json的所有注解 属性不存在不会抛出异常
   *
   * @param sourceList 源对象集合
   * @param clazz      目标类型
   * @param <T>        泛型标记
   * @return T
   */
  public static <T> List<T> copyWithJson(@Nullable Collection<?> sourceList, Class<T> clazz) {
    return JsonUtil.readList(toJson(sourceList), clazz);
  }

  /**
   * 拷贝对象，支持 Map 和 Bean
   *
   * @param source 源对象
   * @param clazz  类名
   * @param <T>    泛型标记
   * @return T
   */
  public static <T> T copy(@Nullable Object source, Class<T> clazz) {
    if (null == source || null == clazz) return null;
    return BeanUtil.copyProperties(source, clazz);
  }

  /**
   * 拷贝对象，支持 Map 和 Bean
   *
   * @param source     源对象
   * @param targetBean 需要赋值的对象
   */
  public static void copy(@Nullable Object source, @Nullable Object targetBean) {
    if (null == source || null == targetBean) return;
    BeanUtil.copyProperties(source, targetBean);
  }

  /**
   * 拷贝对象，忽略null
   *
   * <p>
   * 支持 map bean copy
   * </p>
   *
   * @param source     源对象
   * @param targetBean 需要赋值的对象
   */
  public static void copyIgnoreNull(@Nullable Object source, @Nullable Object targetBean) {
    BeanUtil.copyProperties(source, targetBean, CopyOptions.create().ignoreNullValue());
  }

  /**
   * 拷贝列表对象
   *
   * <p>
   * 支持 map bean copy
   * </p>
   *
   * @param sourceList  源列表
   * @param targetClazz 转换成的类型
   * @param <T>         泛型标记
   * @return T
   */
  public static <T> List<T> copy(@Nullable Collection<?> sourceList, Class<T> targetClazz) {
    return BeanUtil.copyToList(sourceList, targetClazz);
  }

  public static <T> List<T> copyIgnoreNull(@Nullable Collection<?> sourceList, Class<T> targetClazz) {
    return BeanUtil.copyToList(sourceList, targetClazz, CopyOptions.create().ignoreNullValue());
  }

  /**
   * 拷贝对象，忽略null和空字符
   *
   * @param source the source bean
   * @param clazz  the target bean class
   * @param <T>    泛型标记
   * @return T
   * @throws BeansException if the copying failed
   */
  public static <T> T copyIgnoreBlank(@Nullable Object source, Class<T> clazz) {
    return BeanUtil.copyProperties(source, clazz, getBlankPropertyNames(source));
  }

  private static String[] getBlankPropertyNames(Object source) {
    Set<String> emptyNames = new HashSet<>();
    final BeanWrapper src = new BeanWrapperImpl(source);
    PropertyDescriptor[] pds = src.getPropertyDescriptors();
    for (PropertyDescriptor pd : pds) {
      Object srcValue = src.getPropertyValue(pd.getName());
      //如果是null，忽略此属性
      if (srcValue == null) {
        emptyNames.add(pd.getName());
      }
      //如果是空字符串，忽略此属性
      else if (srcValue instanceof String && ((String) srcValue).isEmpty()) {
        emptyNames.add(pd.getName());
      }
    }
    String[] result = new String[emptyNames.size()];
    return emptyNames.toArray(result);
  }

  /**
   * json文本转Map<String,Object>
   * @author lvweijie
   * @date 2024/7/17 17:52
   * @param json json文本
   * @return java.util.Map<java.lang.String,java.lang.Object>
   */
  public static Map<String, Object> toMap(String json) {
    return JsonUtil.toMap(json);
  }

  /**
   * 将bean的部分属性转换成map<br>
   * 可选拷贝哪些属性值，默认是不忽略值为{@code null}的值的。
   *
   * @param bean       bean
   * @param properties 需要拷贝的属性值，{@code null}或空表示拷贝所有值
   * @return Map
   * @since 5.8.0
   */
  public static Map<String, Object> toMap(@Nullable Object bean, String... properties) {
    return BeanUtil.beanToMap(bean, properties);
  }

  /**
   * 将map 转为 bean
   *
   * @param beanMap   map
   * @param valueType 对象类型
   * @param <T>       泛型标记
   * @return {T}
   */
  public static <T> T toBean(Map<String, Object> beanMap, Class<T> valueType) {
    return BeanUtil.toBean(beanMap, valueType);
  }

  /**
   * 简单判断是否json字符串格式的文本
   *
   * @param str 入参字符串
   * @return boolean
   */
  public static boolean isJsonStr(String str) {
    return StringUtil.isJsonStr(str);
  }

  /**
   * 简单判断是否json对象格式的文本
   *
   * @param str 入参字符串
   * @return boolean
   */
  public static boolean isJsonObject(String str) {
    return StringUtil.isJsonObject(str);
  }

  /**
   * 简单判断是否json数组格式的文本
   *
   * @param str 入参字符串
   * @return boolean
   */
  public static boolean isJsonArray(String str) {
    return StringUtil.isJsonStr(str);
  }

  /**
   * 清空HTML标记
   *
   * @param content 内容
   * @return String
   */
  public static String cleanHtmlTag(String content) {
    return content.replaceAll("(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)", "");
  }

  /**
   * 获取本机IP
   *
   * @author lvweijie
   * @date 2024/8/9 22:01
   * @return java.lang.String
   */
  public static String getLocalIP(){
    try{
      String host = InetAddress.getLocalHost().getHostAddress();
      String port = SpringUtil.getProperty("server.port");
      return host + ":" + port;
    }catch (Exception e){
      log.warn("Func.getLocalIP error:"+e.getMessage(), e);
      return StringPool.EMPTY;
    }
  }

  /**
   * 统计中文和英文word数量，中文一个汉字和英文一个单词算一个word, 其他数字和特殊字符不算在内
   *
   * @author lvweijie
   * @date 2024/8/20 10:08
   * @param text text
   * @return long
   */
  public static int countWord(String text){
    if(isBlank(text)) return 0;
    if (Func.isBlank(text)) return 0;
    int[] count = {0};
    String[] str = {StringPool.EMPTY};
    text.chars().forEach(s -> {
      char ch = (char) s;
      if (CharUtil.isChineseCharacter(ch)) {
        count[0]++;
        if (Func.isNotBlank(str[0])) {
          count[0]++;
          str[0] = StringPool.EMPTY;
        }
      }else if (Character.isLetter(ch)) {
        str[0] += ch;
      } else {
        if (Func.isNotBlank(str[0])) {
          count[0]++;
          str[0] = StringPool.EMPTY;
        }
      }
    });
    if (Func.isNotBlank(str[0])) {
      count[0]++;
      str[0] = StringPool.EMPTY;
    }
    return count[0];
  }

  /**
   * 按word下标截取text文本, 中文一个汉字和英文一个单词算一个word
   *
   * @author lvweijie
   * @date 2024/8/20 15:01
   * @param text  文本
   * @param wordStart  0开始，包含
   * @param wordEnd   0开始，不包含
   * @return java.lang.String 截取后的文本
   */
  public static String subStringByWord(String text, int wordStart, int wordEnd) {
    if (wordStart < 0) {
      wordStart = 0;
    }
    if (wordEnd < 0) {
      wordEnd = 0;
    }
    if (wordStart == wordEnd) return StringPool.EMPTY;

    StringBuilder sb = new StringBuilder();

    int[] count = {0};
    String[] str = {StringPool.EMPTY};

    String firstWord = StringPool.EMPTY;

    for (int s : text.chars().toArray()) {
      char ch = (char) s;
      if (CharUtil.isChineseCharacter(ch)) {
        if (Func.isNotBlank(str[0])) {
          count[0]++;
          if (count[0] - 1 >= wordStart) {
            if (count[0] - 1 == wordStart) {
              firstWord = str[0];
            }
            sb.append(str[0]);
          }
          str[0] = StringPool.EMPTY;
          if (count[0] >= wordEnd) {
            break;
          }
        }
        count[0]++;
        if (count[0] - 1 >= wordStart) {
          if (count[0] - 1 == wordStart) {
            firstWord = String.valueOf(ch);
          }
          sb.append(ch);
        }
        if (count[0] >= wordEnd) {
          break;
        }
      } else if (Character.isLetter(ch)) {
        str[0] += ch;
      } else {
        if (Func.isNotBlank(str[0])) {
          count[0]++;
          if (count[0] - 1 >= wordStart) {
            if (count[0] - 1 == wordStart) {
              firstWord = str[0];
            }
            sb.append(str[0]);
          }
          str[0] = StringPool.EMPTY;
          if (count[0] >= wordEnd) {
            break;
          }
        }
        sb.append(ch);
      }
    }
    if (Func.isNotBlank(str[0])) {
      count[0]++;
      if (count[0] - 1 >= wordStart) {
        if (count[0] - 1 == wordStart) {
          firstWord = str[0];
        }
        sb.append(str[0]);
      }
    }

    String result = sb.toString();
    if (Func.isNotBlank(firstWord)) {
      result = result.substring(result.indexOf(firstWord));
    }
    return result;
  }

  /**
   * 移除多余空格：
   * 1. \t ' ' => ''
   * 2. \r\n \n\n... => \n
   *
   * @author lvweijie
   * @date 2024/8/20 10:40
   * @param text  text
   * @return java.lang.String
   */
  public static String removeExtraSpaces(String text) {
    StringBuilder sb = new StringBuilder();
    String[] str = {StringPool.EMPTY};
    text.chars().forEach(s -> {
      char ch = (char) s;
      if (CharPool.TAB == ch || CharPool.SPACE == ch) {
        if (!StringPool.EMPTY.equals(str[0])) {
          sb.append(CharPool.NEWLINE);
          str[0] = StringPool.EMPTY;
        }
      } else if (CharPool.RETURN == ch || CharPool.NEWLINE == ch) {
        str[0] += ch;
      } else {
        if (!StringPool.EMPTY.equals(str[0])) {
          sb.append(CharPool.NEWLINE);
          str[0] = StringPool.EMPTY;
        }
        sb.append(ch);
      }
    });
    return sb.toString();
  }
}
