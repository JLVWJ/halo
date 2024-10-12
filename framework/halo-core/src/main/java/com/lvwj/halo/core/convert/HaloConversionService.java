package com.lvwj.halo.core.convert;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * 类型转换服务，添加了 IEnum 转换
 *
 * @author lvwj
 */
public class HaloConversionService extends ApplicationConversionService {

  private static final HaloConversionService SHARED_INSTANCE = new HaloConversionService();

  public HaloConversionService() {
    this(null);
  }

  public HaloConversionService(@Nullable StringValueResolver embeddedValueResolver) {
    super(embeddedValueResolver);
    super.addConverter(new EnumToStringConverter());
    super.addConverter(new StringToEnumConverter());
  }

  public static GenericConversionService getInstance() {
    return SHARED_INSTANCE;
  }
}
