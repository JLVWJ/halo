package com.lvwj.halo.log.access.webmvc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * @author lvweijie
 * @date 2024年07月06日 10:05
 */
@Slf4j
@ConditionalOnWebApplication(type = Type.SERVLET)
public class WebMvcAccessLogFilter extends OncePerRequestFilter {

  /**
   * 由于此处不需要调用beforeRequest和afterRequest（避免创建无用的对象），因此重写该方法
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    // 包装HttpServletRequest解决InputStream无法重复读的问题
    ContentCachingRequestWrapper requestWrapper = wrapRequest(request);

    WebMvcAccessLogger accessLogger = WebMvcAccessLogger.newInstance(requestWrapper);

    try {
      // 由于被@ControllerAdvice拦截并处理，此处catch exception没效果
      // 因此在WebMvcAccessLogger通过request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE)解决
      filterChain.doFilter(requestWrapper, response);
    } finally {
      accessLogger.log(requestWrapper, response);
    }
  }

  private ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
    if (request instanceof ContentCachingRequestWrapper) {
      return (ContentCachingRequestWrapper) request;
    }
    return new ContentCachingRequestWrapper(request);
  }
}