package com.lvwj.halo.core.jackson.module;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.lvwj.halo.common.utils.DateTimeUtil;
import com.lvwj.halo.core.jackson.serializer.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * java8 时间默认序列化
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-19 10:44
 */
public class JavaTimeModule extends SimpleModule {

  public static final JavaTimeModule FORMAT = new JavaTimeModule();

  public static final JavaTimeModule TIMESTAMP = new JavaTimeModule(true);

  public JavaTimeModule() {
    this(false);
  }

  public JavaTimeModule(boolean isTimestamp) {
    super(PackageVersion.VERSION);
    if (isTimestamp) {
      this.addSerializer(LocalDateTime.class, new LocalDateTimeToUnixTimeSerializer());
      this.addSerializer(LocalDate.class, new LocalDateToUnixTimeSerializer());
      this.addDeserializer(LocalDateTime.class, new LocalDateTimeFromUnixTimeDeserializer());
      this.addDeserializer(LocalDate.class, new LocalDateFromUnixTimeDeserializer());
    } else {
      this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeUtil.DATETIME_FORMAT));
      this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeUtil.DATE_FORMAT));
      this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeUtil.TIME_FORMAT));
      this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeUtil.DATETIME_FORMAT));
      this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeUtil.DATE_FORMAT));
      this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeUtil.TIME_FORMAT));
    }
  }
}
