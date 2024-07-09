package com.lvwj.halo.core.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * local date time -> unix time
 *
 */
public class LocalDateToUnixTimeSerializer extends JsonSerializer<LocalDate> {

  @Override
  public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    if (value != null) {
      long timestamp = value.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
      gen.writeNumber(timestamp);
    }
  }
}
