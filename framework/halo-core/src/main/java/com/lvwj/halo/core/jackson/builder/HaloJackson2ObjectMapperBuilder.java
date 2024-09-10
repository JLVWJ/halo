package com.lvwj.halo.core.jackson.builder;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.*;
import com.lvwj.halo.core.jackson.module.BigNumberModule;
import com.lvwj.halo.core.jackson.module.JavaTimeModule;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Map;
import java.util.TimeZone;

import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER;

public class HaloJackson2ObjectMapperBuilder extends Jackson2ObjectMapperBuilder {

  /**
   * @param objectMapper the ObjectMapper to configure
   */
  @Override
  public void configure(ObjectMapper objectMapper) {

    super.configure(objectMapper);

    // 忽略无法转换的对象
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    // 有未知的字段无法映射到模型，选择忽略
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // 对象的所有字段全部列入
    objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    // 单引号处理
    objectMapper.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true);
    objectMapper.readerFor(Map.class).withFeatures(ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);
    // 允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
    objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
    objectMapper.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
    objectMapper.findAndRegisterModules();
    // 注册Java时间相关的序列化模块
    objectMapper.registerModule(JavaTimeModule.FORMAT);
    // 注册大整数的序列化模块
    objectMapper.registerModule(BigNumberModule.INSTANCE);
    objectMapper.setLocale(LocaleContextHolder.getLocale());
    objectMapper.setTimeZone(TimeZone.getDefault());
    // 属性命名策略定义
    String nameStrategy = SpringUtil.getProperty("halo.jackson.property-naming-strategy");
    PropertyNamingStrategy strategy = getNameStrategy(nameStrategy);
    if (strategy != null) {
      objectMapper.setPropertyNamingStrategy(strategy);
    }
  }

  private PropertyNamingStrategy getNameStrategy(String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }
    return switch (name) {
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
}
