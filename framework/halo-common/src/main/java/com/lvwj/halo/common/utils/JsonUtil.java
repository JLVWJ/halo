package com.lvwj.halo.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.lvwj.halo.common.constants.DateTimeConstant;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER;

/**
 * Jackson工具类
 */
public class JsonUtil {

  /**
   * 将对象序列化成json字符串
   *
   * @param value javaBean
   * @return jsonString json字符串
   */
  public static <T> String toJson(T value) {
    try {
      return getInstance().writeValueAsString(value);
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * 将对象序列化成 json byte 数组
   *
   * @param object javaBean
   * @return jsonString json字符串
   */
  public static byte[] toJsonAsBytes(Object object) {
    try {
      return getInstance().writeValueAsBytes(object);
    } catch (JsonProcessingException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 将json反序列化成对象
   *
   * @param content   content
   * @param valueType class
   * @param <T>       T 泛型标记
   * @return Bean
   */
  public static <T> T parse(String content, Class<T> valueType) {
    try {
      return getInstance().readValue(content, valueType);
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * 将json反序列化成对象
   *
   * @param content       content
   * @param typeReference 泛型类型
   * @param <T>           T 泛型标记
   * @return Bean
   */
  public static <T> T parse(String content, TypeReference<T> typeReference) {
    try {
      return getInstance().readValue(content, typeReference);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 将json byte 数组反序列化成对象
   *
   * @param bytes     json bytes
   * @param valueType class
   * @param <T>       T 泛型标记
   * @return Bean
   */
  public static <T> T parse(byte[] bytes, Class<T> valueType) {
    try {
      return getInstance().readValue(bytes, valueType);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }


  /**
   * 将json反序列化成对象
   *
   * @param bytes         bytes
   * @param typeReference 泛型类型
   * @param <T>           T 泛型标记
   * @return Bean
   */
  public static <T> T parse(byte[] bytes, TypeReference<T> typeReference) {
    try {
      return getInstance().readValue(bytes, typeReference);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 将json反序列化成对象
   *
   * @param in        InputStream
   * @param valueType class
   * @param <T>       T 泛型标记
   * @return Bean
   */
  public static <T> T parse(InputStream in, Class<T> valueType) {
    try {
      return getInstance().readValue(in, valueType);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 将json反序列化成对象
   *
   * @param in            InputStream
   * @param typeReference 泛型类型
   * @param <T>           T 泛型标记
   * @return Bean
   */
  public static <T> T parse(InputStream in, TypeReference<T> typeReference) {
    try {
      return getInstance().readValue(in, typeReference);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 将json反序列化成List对象
   *
   * @param content      content
   * @param valueTypeRef class
   * @param <T>          T 泛型标记
   * @return List<T>
   */
  public static <T> List<T> parseArray(String content, Class<T> valueTypeRef) {
    try {
      if (!StringUtil.startsWithIgnoreCase(content, StringPool.LEFT_SQ_BRACKET)) {
        content = StringPool.LEFT_SQ_BRACKET + content + StringPool.RIGHT_SQ_BRACKET;
      }

      if (ClassUtil.isSimpleType(valueTypeRef)) {
        return getInstance().readValue(content,
                new TypeReference<>() {
                });
      }

      List<Map<String, Object>> list = getInstance().readValue(content,
              new TypeReference<>() {
              });

      List<T> result = new ArrayList<>();
      for (Map<String, Object> map : list) {
        result.add(toPojo(map, valueTypeRef));
      }
      return result;
    } catch (IOException e) {
    }
    return null;
  }

  /**
   * 将json字符串转成 JsonNode
   *
   * @param jsonString jsonString
   * @return jsonString json字符串
   */
  public static JsonNode readTree(String jsonString) {
    Objects.requireNonNull(jsonString, "jsonString is null");
    try {
      return getInstance().readTree(jsonString);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 将json字符串转成 JsonNode
   *
   * @param in InputStream
   * @return jsonString json字符串
   */
  public static JsonNode readTree(InputStream in) {
    Objects.requireNonNull(in, "InputStream in is null");
    try {
      return getInstance().readTree(in);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 将json字符串转成 JsonNode
   *
   * @param content content
   * @return jsonString json字符串
   */
  public static JsonNode readTree(byte[] content) {
    Objects.requireNonNull(content, "byte[] content is null");
    try {
      return getInstance().readTree(content);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 将json字符串转成 JsonNode
   *
   * @param jsonParser JsonParser
   * @return jsonString json字符串
   */
  public static JsonNode readTree(JsonParser jsonParser) {
    Objects.requireNonNull(jsonParser, "jsonParser is null");
    try {
      return getInstance().readTree(jsonParser);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 封装 map type
   *
   * @param keyClass   key 类型
   * @param valueClass value 类型
   * @return MapType
   */
  public static MapType getMapType(Class<?> keyClass, Class<?> valueClass) {
    return getInstance().getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
  }

  /**
   * 封装 map type
   *
   * @param elementClass 集合值类型
   * @return CollectionLikeType
   */
  public static CollectionLikeType getListType(Class<?> elementClass) {
    return getInstance().getTypeFactory().constructCollectionLikeType(List.class, elementClass);
  }

  /**
   * 读取集合
   *
   * @param content      bytes
   * @param elementClass elementClass
   * @param <T>          泛型
   * @return 集合
   */
  public static <T> List<T> readList(@Nullable byte[] content, Class<T> elementClass) {
    if (ObjectUtil.isEmpty(content)) {
      return Collections.emptyList();
    }
    try {
      return getInstance().readValue(content, getListType(elementClass));
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 读取集合
   *
   * @param content      InputStream
   * @param elementClass elementClass
   * @param <T>          泛型
   * @return 集合
   */
  public static <T> List<T> readList(@Nullable InputStream content, Class<T> elementClass) {
    if (content == null) {
      return Collections.emptyList();
    }
    try {
      return getInstance().readValue(content, getListType(elementClass));
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 读取集合
   *
   * @param content      bytes
   * @param elementClass elementClass
   * @param <T>          泛型
   * @return 集合
   */
  public static <T> List<T> readList(@Nullable String content, Class<T> elementClass) {
    if (ObjectUtil.isEmpty(content)) {
      return Collections.emptyList();
    }
    try {
      return getInstance().readValue(content, getListType(elementClass));
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 读取集合
   *
   * @param content    bytes
   * @param keyClass   key类型
   * @param valueClass 值类型
   * @param <K>        泛型
   * @param <V>        泛型
   * @return 集合
   */
  public static <K, V> Map<K, V> readMap(@Nullable byte[] content, Class<?> keyClass, Class<?> valueClass) {
    if (ObjectUtil.isEmpty(content)) {
      return Collections.emptyMap();
    }
    try {
      return getInstance().readValue(content, getMapType(keyClass, valueClass));
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 读取集合
   *
   * @param content    InputStream
   * @param keyClass   key类型
   * @param valueClass 值类型
   * @param <K>        泛型
   * @param <V>        泛型
   * @return 集合
   */
  public static <K, V> Map<K, V> readMap(@Nullable InputStream content, Class<?> keyClass, Class<?> valueClass) {
    if (ObjectUtil.isEmpty(content)) {
      return Collections.emptyMap();
    }
    try {
      return getInstance().readValue(content, getMapType(keyClass, valueClass));
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 读取集合
   *
   * @param content    bytes
   * @param keyClass   key类型
   * @param valueClass 值类型
   * @param <K>        泛型
   * @param <V>        泛型
   * @return 集合
   */
  public static <K, V> Map<K, V> readMap(@Nullable String content, Class<?> keyClass, Class<?> valueClass) {
    if (ObjectUtil.isEmpty(content)) {
      return Collections.emptyMap();
    }
    try {
      return getInstance().readValue(content, getMapType(keyClass, valueClass));
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * jackson 的类型转换
   *
   * @param fromValue   来源对象
   * @param toValueType 转换的类型
   * @param <T>         泛型标记
   * @return 转换结果
   */
  public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
    return getInstance().convertValue(fromValue, toValueType);
  }

  /**
   * jackson 的类型转换
   *
   * @param fromValue   来源对象
   * @param toValueType 转换的类型
   * @param <T>         泛型标记
   * @return 转换结果
   */
  public static <T> T convertValue(Object fromValue, JavaType toValueType) {
    return getInstance().convertValue(fromValue, toValueType);
  }

  /**
   * jackson 的类型转换
   *
   * @param fromValue      来源对象
   * @param toValueTypeRef 泛型类型
   * @param <T>            泛型标记
   * @return 转换结果
   */
  public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
    return getInstance().convertValue(fromValue, toValueTypeRef);
  }

  /**
   * tree 转对象
   *
   * @param treeNode  TreeNode
   * @param valueType valueType
   * @param <T>       泛型标记
   * @return 转换结果
   */
  public static <T> T treeToValue(TreeNode treeNode, Class<T> valueType) {
    try {
      return getInstance().treeToValue(treeNode, valueType);
    } catch (JsonProcessingException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 对象转为 json node
   *
   * @param value 对象
   * @return JsonNode
   */
  public static JsonNode valueToTree(@Nullable Object value) {
    return getInstance().valueToTree(value);
  }

  /**
   * 判断是否可以序列化
   *
   * @param value 对象
   * @return 是否可以序列化
   */
  public static boolean canSerialize(@Nullable Object value) {
    if (value == null) {
      return true;
    }
    return getInstance().canSerialize(value.getClass());
  }

  public static Map<String, Object> toMap(String content) {
    try {
      return getInstance().readValue(content, Map.class);
    } catch (IOException e) {
    }
    return null;
  }

  public static <T> Map<String, T> toMap(String content, Class<T> valueTypeRef) {
    try {
      if (ClassUtil.isSimpleType(valueTypeRef)) {
        return getInstance().readValue(content,
                new TypeReference<>() {
                });
      }
      Map<String, Map<String, Object>> map = getInstance().readValue(content,
              new TypeReference<>() {
              });
      Map<String, T> result = new HashMap<>(16);
      for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
        result.put(entry.getKey(), toPojo(entry.getValue(), valueTypeRef));
      }
      return result;
    } catch (IOException e) {
    }
    return null;
  }

  public static <T> T toPojo(Map fromValue, Class<T> toValueType) {
    return getInstance().convertValue(fromValue, toValueType);
  }

  public static ObjectMapper getInstance() {
    return JacksonHolder.INSTANCE;
  }

  private static class JacksonHolder {

    private static final ObjectMapper INSTANCE = new JacksonObjectMapper();
  }

  private static class JacksonObjectMapper extends ObjectMapper {

    public JacksonObjectMapper(ObjectMapper src) {
      super(src);
    }

    public JacksonObjectMapper() {
      super();
      Locale locale = LocaleContextHolder.getLocale();
      //设置地点
      super.setLocale(locale);
      //去掉默认的时间戳格式
      super.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      //设置为零时区
      super.setTimeZone(DateTimeConstant.TIME_ZONE_UTC);
      //序列化时，日期的统一格式
      super.setDateFormat(new SimpleDateFormat(DateTimeConstant.PATTERN_DATETIME, locale));
      //禁用时区调整特性
      super.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
      //单引号处理
      super.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true);
      super.readerFor(Map.class).withFeatures(ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);
      // 允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
      super.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
      super.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
      //失败处理
      super.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      super.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      super.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      //日期格式化
      super.registerModule(new JavaTimeModule());
      super.registerModule(new BigNumberModule());
      super.findAndRegisterModules();
    }

    @Override
    public ObjectMapper copy() {
      return new JacksonObjectMapper(this);
    }
  }

  private static class BigNumberModule extends SimpleModule {
    public BigNumberModule() {
      super(BigNumberModule.class.getName());
      // Long 和 BigInteger 采用定制的逻辑序列化，避免超过js的精度
      this.addSerializer(Long.class, ToStringSerializer.instance);
      this.addSerializer(Long.TYPE, ToStringSerializer.instance);
      this.addSerializer(BigInteger.class, ToStringSerializer.instance);
      // BigDecimal 采用 toString 避免精度丢失。
      //this.addSerializer(BigDecimal.class, ToStringSerializer.instance);
    }
  }

  private static class JavaTimeModule extends SimpleModule {

    public JavaTimeModule() {
      super(PackageVersion.VERSION);
      this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeConstant.FORMAT_DATETIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeConstant.FORMAT_DATE.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeConstant.FORMAT_TIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeConstant.FORMAT_DATETIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeConstant.FORMAT_DATE.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeConstant.FORMAT_TIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
    }
  }
}
