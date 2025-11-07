package com.lvwj.halo.core.jackson.builder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.*;
import com.lvwj.halo.common.constants.DateTimeConstant;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.core.jackson.module.BigNumberModule;
import com.lvwj.halo.core.jackson.module.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER;

@Slf4j
public class HaloJackson2ObjectMapperBuilder extends Jackson2ObjectMapperBuilder {

  // 配置前缀（用于读取 application 配置）
  private static final String PROPERTY_NAMING_STRATEGY_KEY = "halo.jackson.property-naming-strategy";
  private static final String TIMESTAMP_KEY = "halo.jackson.time.is-timestamp";

  // 注入 Spring 环境变量（替代静态工具类，更符合 Spring 依赖注入规范）
  private final Environment environment;

  /**
   * 构造器注入 Environment（通过 Spring 自动注入）
   */
  public HaloJackson2ObjectMapperBuilder(Environment environment) {
    this.environment = environment;
  }

  /**
   * @param objectMapper the ObjectMapper to configure
   */
  @Override
  public void configure(ObjectMapper objectMapper) {
    // 1. 先执行父类配置（保留 Spring 默认配置）
    super.configure(objectMapper);

    try {
      // 2. 配置基础序列化/反序列化特性
      configureBaseFeatures(objectMapper);

      // 3. 配置时间相关（时区、格式统一）
      configureTimeSettings(objectMapper);

      // 4. 配置模块（先默认模块，再自定义模块，确保自定义覆盖默认）
      configureModules(objectMapper);

      // 5. 配置 Locale（多语言支持）
      configureLocale(objectMapper);

      // 6. 配置属性命名策略（支持外部配置）
      configurePropertyNamingStrategy(objectMapper);

      log.info("Jackson ObjectMapper 配置完成，命名策略：{}，时区：{}",
              getConfiguredNamingStrategy(), DateTimeConstant.TIME_ZONE_UTC.getID());
    } catch (Exception e) {
      log.error("Jackson ObjectMapper 配置失败", e);
      throw new RuntimeException("Jackson 配置初始化异常", e);
    }
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
    String timestamp = environment.getProperty(TIMESTAMP_KEY, "false");
    objectMapper.registerModule(new JavaTimeModule(Func.toBoolean(timestamp)));
    // 大数字模块（处理 Long/BigInteger/BigDecimal 避免 JS 精度丢失）
    objectMapper.registerModule(BigNumberModule.INSTANCE);

    log.debug("已注册自定义模块：JavaTimeModule、BigNumberModule");
  }

  /**
   * 4. 配置 Locale（多语言支持，影响日期格式化的语言环境）
   */
  private void configureLocale(ObjectMapper objectMapper) {
    Locale locale = LocaleContextHolder.getLocale();
    objectMapper.setLocale(locale);
    log.debug("Jackson 配置 Locale：{}", locale);
  }

  /**
   * 5. 配置属性命名策略（支持通过配置文件动态指定）
   */
  private void configurePropertyNamingStrategy(ObjectMapper objectMapper) {
    // 从配置文件获取命名策略（默认 null，使用 Jackson 默认策略）
    String nameStrategy = environment.getProperty(PROPERTY_NAMING_STRATEGY_KEY);
    if (!StringUtils.hasText(nameStrategy)) {
      log.debug("未配置属性命名策略，使用默认策略");
      return;
    }

    // 解析命名策略
    PropertyNamingStrategy strategy = getPropertyNamingStrategy(nameStrategy);
    if (strategy != null) {
      objectMapper.setPropertyNamingStrategy(strategy);
      log.debug("已应用属性命名策略：{}", nameStrategy);
    } else {
      log.warn("无效的属性命名策略配置：{}，支持的策略：SNAKE_CASE、LOWER_CAMEL_CASE、UPPER_CAMEL_CASE、UPPER_SNAKE_CASE、LOWER_CASE、KEBAB_CASE、LOWER_DOT_CASE", nameStrategy);
    }
  }

  /**
   * 解析命名策略字符串为枚举实例
   */
  private PropertyNamingStrategy getPropertyNamingStrategy(String name) {
    return switch (name.trim().toUpperCase()) {
      case "SNAKE_CASE" -> PropertyNamingStrategies.SNAKE_CASE;
      case "LOWER_CAMEL_CASE" -> PropertyNamingStrategies.LOWER_CAMEL_CASE;
      case "UPPER_CAMEL_CASE" -> PropertyNamingStrategies.UPPER_CAMEL_CASE;
      case "UPPER_SNAKE_CASE" -> PropertyNamingStrategies.UPPER_SNAKE_CASE;
      case "LOWER_CASE" -> PropertyNamingStrategies.LOWER_CASE;
      case "KEBAB_CASE" -> PropertyNamingStrategies.KEBAB_CASE;
      case "LOWER_DOT_CASE" -> PropertyNamingStrategies.LOWER_DOT_CASE;
      default -> null;
    };
  }

  /**
   * 获取已配置的命名策略（用于日志输出）
   */
  private String getConfiguredNamingStrategy() {
    String nameStrategy = environment.getProperty(PROPERTY_NAMING_STRATEGY_KEY);
    return StringUtils.hasText(nameStrategy) ? nameStrategy : "默认策略";
  }
}
