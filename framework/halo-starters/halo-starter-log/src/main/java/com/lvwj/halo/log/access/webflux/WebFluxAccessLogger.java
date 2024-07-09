package com.lvwj.halo.log.access.webflux;


import com.lvwj.halo.log.access.AccessLogger;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;

import java.net.InetSocketAddress;
import java.util.Map;

public class WebFluxAccessLogger extends AccessLogger<ServerHttpRequest, ServerHttpResponse> {

  // 内部异常的attribute，详见org.springframework.boot.web.reactive.error.DefaultErrorAttributes
  private static final String ERROR_INTERNAL_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";
  private static final String REQUEST_BODY_CACHE = ServerHttpRequest.class.getName() + ".body";

  private final ServerWebExchange exchange;

  public WebFluxAccessLogger(ServerWebExchange exchange) {
    super(exchange.getRequest());

    this.exchange = exchange;
  }

  /**
   * 创建实例
   */
  public static WebFluxAccessLogger newInstance(ServerWebExchange exchange) {
    return new WebFluxAccessLogger(exchange);
  }

  @Override
  protected String getHttpMethod(ServerHttpRequest request) {
    return request.getMethod().name();
  }

  @Override
  protected String getClientIP(ServerHttpRequest request) {
    InetSocketAddress address = request.getRemoteAddress();
    if (address != null) {
      return address.getAddress().toString();
    }

    return null;
  }

  @Override
  protected String getRequestURI(ServerHttpRequest request) {
    return request.getPath().value();
  }

  /**
   * 优先获取request body，没有获取query param
   */
  @Override
  protected String getRequestData(ServerHttpRequest request) {
    String requestBody = getCachedRequestBody(exchange);
    if (StringUtils.hasLength(requestBody)) {
      return requestBody;
    }

    return buildParameterValue(request);
  }

  @Override
  protected String getPathPattern(ServerHttpRequest request) {
    Object pathPattern = exchange.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

    if (pathPattern instanceof PathPattern) {
      return ((PathPattern) pathPattern).getPatternString();
    }

    return null;
  }

  @Override
  protected Integer getStatusCode(ServerHttpResponse response) {
    return response.getRawStatusCode();
  }

  @Override
  protected Throwable getException(ServerHttpRequest request) {
    Object exception = exchange.getAttribute(ERROR_INTERNAL_ATTRIBUTE);
    if (exception instanceof Throwable) {
      return (Throwable) exception;
    }

    return null;
  }

  @Override
  protected HandlerMethod getHandlerMethod(ServerHttpRequest request) {
    Object handlerObj = exchange.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
    if (handlerObj instanceof HandlerMethod) {
      return (HandlerMethod) handlerObj;
    }

    return null;
  }

  /**
   * 拼接参数为a=b&c=d
   */
  private String buildParameterValue(ServerHttpRequest request) {
    MultiValueMap multiValueMap = request.getQueryParams();
    if (CollectionUtils.isEmpty(multiValueMap)) {
      return null;
    }

    Map queryParamValueMap = multiValueMap.toSingleValueMap();

    StringBuilder builder = new StringBuilder();
    queryParamValueMap.keySet().stream().forEach(key -> {
      if (!builder.isEmpty()) {
        builder.append(AND_SYMBOL);
      }

      builder.append(key).append(EQUAL_SYMBOL).append(queryParamValueMap.get(key));
    });

    return builder.toString();
  }

  private String getCachedRequestBody(ServerWebExchange exchange) {
    StringBuilder requestBody = exchange.getAttribute(REQUEST_BODY_CACHE);
    if (requestBody == null) {
      return null;
    }
    return requestBody.toString();
  }
}
