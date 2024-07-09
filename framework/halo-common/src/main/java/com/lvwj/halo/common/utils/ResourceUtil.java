package com.lvwj.halo.common.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;

/**
 * 资源工具类
 */
public class ResourceUtil extends ResourceUtils {

  public static final String HTTP_REGEX = "^https?:.+$";
  public static final String FTP_URL_PREFIX = "ftp:";

  /**
   * 获取资源
   * <p>
   * 支持一下协议：
   * <p>
   * 1. classpath: 2. file: 3. ftp: 4. http: and https: 5. classpath*: 6. C:/dir1/ and /Users/lcm
   * </p>
   *
   * @param resourceLocation 资源路径
   * @return {Resource}
   * @throws IOException IOException
   */
  public static Resource getResource(String resourceLocation) throws IOException {
    Assert.notNull(resourceLocation, "Resource location must not be null");
    if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
      return new ClassPathResource(resourceLocation);
    }
    if (resourceLocation.startsWith(FTP_URL_PREFIX)) {
      return new UrlResource(resourceLocation);
    }
    if (resourceLocation.matches(HTTP_REGEX)) {
      return new UrlResource(resourceLocation);
    }
    if (resourceLocation.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
      return SpringUtil.getApplicationContext().getResource(resourceLocation);
    }
    return new FileSystemResource(resourceLocation);
  }

  public static URL getResourceURL(String resource) {
    return getResourceURL(resource, null);
  }

  public static URL getResourceURL(String resource, Class<?> baseClass) {
    return null != baseClass ? baseClass.getResource(resource) : ClassLoaderUtil.getClassLoader().getResource(resource);
  }
}
