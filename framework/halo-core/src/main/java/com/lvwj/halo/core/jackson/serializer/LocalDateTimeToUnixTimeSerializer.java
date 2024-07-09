package com.lvwj.halo.core.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * local date time -> unix time
 *
 */
public class LocalDateTimeToUnixTimeSerializer extends JsonSerializer<LocalDateTime> {

  @Override
  public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    if (value != null) {
      long milliSecond = value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
      gen.writeNumber(milliSecond);
    }
  }
}
