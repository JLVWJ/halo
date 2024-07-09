package com.lvwj.halo.core.jackson.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * unix time -> local date time
 *
 */
public class LocalDateTimeFromUnixTimeDeserializer extends JsonDeserializer<LocalDateTime> {

  @Override
  public LocalDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
    NumberDeserializers.LongDeserializer longDeserializer = new NumberDeserializers.LongDeserializer(Long.TYPE, 0L);
    Long epoch = longDeserializer.deserialize(p, context);
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault());
  }
}
