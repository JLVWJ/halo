package com.lvwj.halo.core.jackson.module;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.lvwj.halo.common.constants.DateTimeConstant;
import com.lvwj.halo.core.jackson.serializer.LocalDateFromUnixTimeDeserializer;
import com.lvwj.halo.core.jackson.serializer.LocalDateTimeFromUnixTimeDeserializer;
import com.lvwj.halo.core.jackson.serializer.LocalDateTimeToUnixTimeSerializer;
import com.lvwj.halo.core.jackson.serializer.LocalDateToUnixTimeSerializer;

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
      this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeConstant.FORMAT_DATETIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeConstant.FORMAT_DATE.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeConstant.FORMAT_TIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeConstant.FORMAT_DATETIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeConstant.FORMAT_DATE.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
      this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeConstant.FORMAT_TIME.withZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId())));
    }
  }
}
