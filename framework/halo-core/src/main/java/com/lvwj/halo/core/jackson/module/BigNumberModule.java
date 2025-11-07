package com.lvwj.halo.core.jackson.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 大整数序列化为 String 字符串，避免浏览器丢失精度
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-11-25 11:04
 */
public class BigNumberModule extends SimpleModule {

  public static final BigNumberModule INSTANCE = new BigNumberModule();

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
      public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
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
