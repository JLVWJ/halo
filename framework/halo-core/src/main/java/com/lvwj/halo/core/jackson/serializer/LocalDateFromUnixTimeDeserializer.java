package com.lvwj.halo.core.jackson.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.lvwj.halo.common.constants.DateTimeConstant;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * unix time -> local date time
 *
 */
public class LocalDateFromUnixTimeDeserializer extends JsonDeserializer<LocalDate> {

  @Override
  public LocalDate deserialize(JsonParser p, DeserializationContext context) throws IOException {
    NumberDeserializers.LongDeserializer longDeserializer = new NumberDeserializers.LongDeserializer(Long.TYPE, 0L);
    Long epoch = longDeserializer.deserialize(p, context);
    return Instant.ofEpochMilli(epoch).atZone(DateTimeConstant.TIME_ZONE_UTC.toZoneId()).toLocalDate();
  }
}
