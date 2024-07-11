package com.lvwj.halo.web.i18n;

import com.lvwj.halo.common.dto.response.PR;
import com.lvwj.halo.common.dto.response.R;
import com.lvwj.halo.core.i18n.I18nUtil;
import com.lvwj.halo.web.annotation.IgnoreI18nResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * 国际化advice，需要在异常拦截之后，确保异常的也能国际化处理
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-05 14:59
 */
@Slf4j
@Order(-40)
@RestControllerAdvice
@ConditionalOnProperty(value = "spring.messages.basename")
public class I18nResponseBodyAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
    Method method = methodParameter.getMethod();
    if (method == null) {
      return false;
    }
    // 如果被IgnoreResponseAdvice标识就不拦截
    if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreI18nResponseBody.class)
            || method.isAnnotationPresent(IgnoreI18nResponseBody.class)) {
      return false;
    }
    String name = method.getReturnType().getName();
    return R.class.getName().equalsIgnoreCase(name) || PR.class.getName().equalsIgnoreCase(name);
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
    if (null == body) {
      return null;
    }
    if (body instanceof R<?> r) {
      try {
        r.setMessage(I18nUtil.getMessage(r.getCode() + "", r.getArgs(), r.getMessage()));
      } catch (Exception e) {
        log.warn("format international message error for code: {}", r.getCode(), e);
      }
    }
    return body;
  }
}
