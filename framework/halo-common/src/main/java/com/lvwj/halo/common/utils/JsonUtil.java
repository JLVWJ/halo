package com.lvwj.halo.common.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
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
import java.math.BigDecimal;
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
    try {
      return SpringUtil.getBean(ObjectMapper.class);
    } catch (Exception ignore) {
      return JacksonHolder.INSTANCE;
    }
  }

  private static class JacksonHolder {
    private static final ObjectMapper INSTANCE = new JacksonObjectMapper();
  }

  private static class JacksonObjectMapper extends ObjectMapper {

    public JacksonObjectMapper() {
      super();
      try {
        // 1. 配置基础序列化/反序列化特性
        configureBaseFeatures(this);

        // 2. 配置时间相关（时区、格式统一）
        configureTimeSettings(this);

        // 3. 配置模块（先默认模块，再自定义模块，确保自定义覆盖默认）
        configureModules(this);

        // 4. 配置 Locale（多语言支持）
        configureLocale(this);

      } catch (Exception e) {
        throw new RuntimeException("Jackson 配置初始化异常", e);
      }
    }

    @Override
    public ObjectMapper copy() {
      return new JacksonObjectMapper();
    }

    /**
     * 1. 配置基础序列化/反序列化特性
     */
    private void configureBaseFeatures(ObjectMapper objectMapper) {
      // 序列化特性：空对象不抛异常（避免无字段类序列化报错）
      objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
      // 禁用默认时间戳格式（使用自定义字符串格式）
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      // 反序列化特性：忽略未知字段（避免 JSON 有多余字段时报错）
      objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      // 禁用时区自动调整（强制使用配置的 UTC 时区）
      objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

      // 宽松的 JSON 解析规则（兼容非标准 JSON 格式）
      objectMapper.enable(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature()); // 允许单引号（如 'name'）
      objectMapper.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature()); // 允许未转义控制字符（如 \t）
      objectMapper.enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature()); // 允许任意字符反斜杠转义
      objectMapper.readerFor(Map.class).withFeatures(ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);

      // 序列化包含策略：默认序列化所有字段（如需排除 null 值，可改为 JsonInclude.Include.NON_NULL）
      objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    /**
     * 2. 配置时间相关（统一时区和格式）
     */
    private void configureTimeSettings(ObjectMapper objectMapper) {
      // 全局时区：统一使用 UTC（避免服务器时区差异导致的时间偏移）
      objectMapper.setTimeZone(DateTimeConstant.TIME_ZONE_UTC);

      // 配置 java.util.Date/Calendar 序列化格式（与 java.time 模块保持一致）
      Locale locale = LocaleContextHolder.getLocale();
      SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeConstant.PATTERN_DATETIME, locale);
      dateFormat.setTimeZone(DateTimeConstant.TIME_ZONE_UTC); // 强制绑定 UTC 时区
      objectMapper.setDateFormat(dateFormat);
    }

    /**
     * 3. 配置模块（注册自定义序列化/反序列化逻辑）
     * 顺序：先自动注册默认模块，再注册自定义模块（确保自定义覆盖默认）
     */
    private void configureModules(ObjectMapper objectMapper) {
      // 第一步：自动注册 Jackson 内置模块（如 jackson-datatype-jsr310、jackson-datatype-jdk8 等）
      objectMapper.findAndRegisterModules();

      // 第二步：注册自定义模块（覆盖默认配置）
      // 时间模块（处理 LocalDateTime/LocalDate 等 java.time 类型）
      objectMapper.registerModule(new JavaTimeModule());
      // 大数字模块（处理 Long/BigInteger/BigDecimal 避免 JS 精度丢失）
      objectMapper.registerModule(new BigNumberModule());
    }

    /**
     * 4. 配置 Locale（多语言支持，影响日期格式化的语言环境）
     */
    private void configureLocale(ObjectMapper objectMapper) {
      Locale locale = LocaleContextHolder.getLocale();
      objectMapper.setLocale(locale);
    }
  }

  private static class BigNumberModule extends SimpleModule {
    public BigNumberModule() {
      super(BigNumberModule.class.getName(), com.fasterxml.jackson.core.Version.unknownVersion());
      // 1. 序列化：Long 和 BigInteger 采用定制的逻辑序列化，避免超过js的精度
      this.addSerializer(Long.class, ToStringSerializer.instance);
      this.addSerializer(Long.TYPE, ToStringSerializer.instance);
      this.addSerializer(BigInteger.class, ToStringSerializer.instance);

      // 2. 序列化：BigDecimal -> 字符串（避免科学计数法 + 保留原始精度，如 0.0000001 不转 1E-7）
      this.addSerializer(BigDecimal.class, new JsonSerializer<>() {
        @Override
        public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
          jsonGenerator.writeString(bigDecimal.toPlainString());
        }
      });
      // 3. 反序列化：字符串 -> BigDecimal（支持从字符串/数字类型 JSON 反序列化）
      this.addDeserializer(BigDecimal.class, new JsonDeserializer<>() {
        public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
          String numStr = jsonParser.getText().trim();
          try {
            return new BigDecimal(numStr);
          } catch (NumberFormatException e) {
            throw new IOException(String.format("BigDecimal 格式错误，实际：%s", numStr), e);
          }
        }
      });
    }
  }

  private static class JavaTimeModule extends SimpleModule {

    public JavaTimeModule() {
      super(JavaTimeModule.class.getName(), com.fasterxml.jackson.core.Version.unknownVersion());
      this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeConstant.FORMAT_DATETIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeConstant.FORMAT_DATE.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeConstant.FORMAT_TIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeConstant.FORMAT_DATETIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeConstant.FORMAT_DATE.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeConstant.FORMAT_TIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
    }
  }
}
