package com.lvwj.halo.core.jackson.module;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

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
    super(BigNumberModule.class.getName());
    // Long 和 BigInteger 采用定制的逻辑序列化，避免超过js的精度
    this.addSerializer(Long.class, ToStringSerializer.instance);
    this.addSerializer(Long.TYPE, ToStringSerializer.instance);
    this.addSerializer(BigInteger.class, ToStringSerializer.instance);
    // BigDecimal 采用 toString 避免精度丢失。
    this.addSerializer(BigDecimal.class, ToStringSerializer.instance);
  }
}
