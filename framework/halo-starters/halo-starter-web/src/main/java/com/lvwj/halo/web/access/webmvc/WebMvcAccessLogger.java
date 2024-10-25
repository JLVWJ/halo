package com.lvwj.halo.web.access.webmvc;

import com.lvwj.halo.web.access.AccessLogger;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * @author lvweijie
 * @date 2024年07月06日 10:05
 */
public final class WebMvcAccessLogger extends AccessLogger<ContentCachingRequestWrapper, HttpServletResponse> {

  // 私有构造器，不允许外部直接new
  private WebMvcAccessLogger(ContentCachingRequestWrapper request) {
    super(request);
  }

  /**
   * 创建实例
   */
  public static WebMvcAccessLogger newInstance(ContentCachingRequestWrapper request) {
    return new WebMvcAccessLogger(request);
  }

  @Override
  protected String getHttpMethod(ContentCachingRequestWrapper request) {
    return request.getMethod();
  }

  @Override
  protected String getClientIP(ContentCachingRequestWrapper request) {
    return request.getRemoteAddr();
  }

  @Override
  protected String getRequestURI(ContentCachingRequestWrapper request) {
    return request.getRequestURI();
  }

  @Override
  protected HandlerMethod getHandlerMethod(ContentCachingRequestWrapper request) {
    Object handlerObj = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
    if (handlerObj instanceof HandlerMethod) {
      return (HandlerMethod) handlerObj;
    }

    return null;
  }

  @Override
  protected String getRequestData(ContentCachingRequestWrapper request) {
    if (request.getContentLength() > 0) {
      return new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
    }
    return buildParameterValue(request);
  }

  @Override
  protected String getPathPattern(ContentCachingRequestWrapper request) {
    Object urlPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

    if (urlPattern != null) {
      return (String) urlPattern;
    }

    return null;
  }

  @Override
  protected Integer getStatusCode(HttpServletResponse response) {
    return response.getStatus();
  }

  @Override
  protected Throwable getException(ContentCachingRequestWrapper request) {
    Object exception = request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);
    if (exception instanceof Throwable) {
      return (Throwable) exception;
    }
    return null;
  }

  /**
   * 构建a=b&c=d的参数格式
   */
  private String buildParameterValue(ContentCachingRequestWrapper request) {
    Enumeration<String> paramNames = request.getParameterNames();
    if (!paramNames.hasMoreElements()) {
      return null;
    }
    StringBuilder builder = new StringBuilder();
    while (paramNames.hasMoreElements()) {
      if (!builder.isEmpty()) {
        builder.append(AND_SYMBOL);
      }
      String paramName = paramNames.nextElement();
      String value = request.getParameter(paramName);
      builder.append(paramName).append(EQUAL_SYMBOL).append(value);
    }
    return builder.toString();
  }
}