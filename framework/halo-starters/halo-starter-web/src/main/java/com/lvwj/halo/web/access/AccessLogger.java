package com.lvwj.halo.web.access;

import com.lvwj.halo.common.exceptions.BusinessException;
import com.lvwj.halo.common.utils.DateTimeUtil;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvweijie
 * @date 2024年07月06日 10:05
 */
@Slf4j
public abstract class AccessLogger<T, R> {

  public static final String AND_SYMBOL = "&";
  public static final String EQUAL_SYMBOL = "=";

  private static final Logger BIZ_LOG = LoggerFactory.getLogger(AccessLog.class);
  // 本地线程上下文
  private static final ThreadLocal<AccessLogger<?, ?>> CONTEXT = new ThreadLocal<>();
  // actionName的格式，类名-方法名
  private static final String ACTION_NAME_FORMAT = "%s-%s";
  // 日志分隔符
  private static final String LOG_SPLITTER = " | ";
  // 最大长度
  private static final int MAX_LENGTH = 50;

  protected AccessLog accessLog;

  public AccessLogger(T request) {
    initAccessLog(request);

    CONTEXT.set(this);
  }

  /**
   * 获取AccessLogger实例
   */
  public static AccessLogger<?, ?> getLogger() {
    return CONTEXT.get();
  }

  /**
   * 获取请求HTTP方法
   */
  protected abstract String getHttpMethod(T request);

  /**
   * 获取客户端IP
   */
  protected abstract String getClientIP(T request);

  /**
   * 获取请求地址
   */
  protected abstract String getRequestURI(T request);

  /**
   * 获取HandlerMethod
   */
  protected abstract HandlerMethod getHandlerMethod(T request);

  /**
   * 获取请求数据
   */
  protected abstract String getRequestData(T request);

  /**
   * 获取匹配路径
   */
  protected abstract String getPathPattern(T request);

  /**
   * 获取匹配路径
   */
  protected abstract Integer getStatusCode(R response);

  /**
   * 设置异常信息
   */
  protected abstract Throwable getException(T request);

  /**
   * 记录日志，此处注意catch异常，出问题不能影响核心流程
   */
  public void log(T request, R response) {
    try {
      // 设置请求数据，如果类或方法上增加@SkipRequestData的注解，则不记录请求数据
      if (!isSkipRequestData(request)) {
        accessLog.setRequestData(getRequestData(request));
      }

      // 设置匹配路径，方便后续针对restful地址统计和观测
      logContext("url_pattern", getPathPattern(request));

      // 设置action
      setAction(getHandlerMethod(request));

      // HTTP状态码
      accessLog.setHttpStatusCode(getStatusCode(response));

      // 设置异常信息
      logException(getException(request));

      // 计算耗时
      accessLog.setElapsed(ChronoUnit.MILLIS.between(accessLog.getCreateTime(), LocalDateTime.now()));

      log.info(buildLog());
    } catch (Exception e) {
      BIZ_LOG.error("record access log error", e);
    } finally {
      // 移除本地线程数据，避免内存泄露
      CONTEXT.remove();
    }
  }

  /**
   * 格式化action
   */
  protected void setAction(HandlerMethod handlerMethod) {
    // 静态资源或者访问的url 404就会出现HandlerMethod为null的情况
    if (handlerMethod == null) {
      return;
    }
    String controllerName = handlerMethod.getBeanType().getSimpleName();
    String methodName = handlerMethod.getMethod().getName();

    String action = String.format(ACTION_NAME_FORMAT, controllerName, methodName);
    accessLog.setAction(action);
  }

  /**
   * 记录上下文字段信息
   */
  public void logContext(String key, String value) {
    if (accessLog.getContext() == null) {
      accessLog.setContext(new HashMap<>());
    }

    accessLog.getContext().put(key, Func.removeLineBreak(value));
  }

  /**
   * 记录异常信息
   */
  public void logException(Throwable e) {
    if (e == null) {
      return;
    }
    // 业务异常为warn级别，其他的非预期的，为error级别
    if (e instanceof BusinessException) {
      accessLog.setResult(AccessLog.ResultType.WARN);
    } else {
      accessLog.setResult(AccessLog.ResultType.ERROR);
    }

    String exceptionMessage = e.getMessage();
    if (StringUtils.hasLength(exceptionMessage) && exceptionMessage.length() > MAX_LENGTH) {
      exceptionMessage = exceptionMessage.substring(0, MAX_LENGTH);
      exceptionMessage = Func.removeLineBreak(exceptionMessage);
    }
    logContext("exception", e.getClass().getName());
    logContext("exception_message", exceptionMessage);
  }

  private String buildLog() {
    StringBuilder logBuilder = new StringBuilder();
    logBuilder.append(DateTimeUtil.formatDateTimeS(accessLog.getCreateTime()))
            .append(LOG_SPLITTER)
            .append(Func.toStrWithEmpty(accessLog.getTraceId(), StringPool.DASH))
            .append(LOG_SPLITTER)
            .append(accessLog.getResult().toString())
            .append(LOG_SPLITTER)
            .append(Func.toStrWithEmpty(accessLog.getAction(), StringPool.DASH))
            .append(LOG_SPLITTER)
            .append(accessLog.getElapsed())
            .append(LOG_SPLITTER)
            .append(Func.toStrWithEmpty(accessLog.getClientIp(), StringPool.DASH))
            .append(LOG_SPLITTER)
            .append(accessLog.getRequestUri())
            .append(LOG_SPLITTER)
            .append(accessLog.getHttpMethod())
            .append(LOG_SPLITTER)
            .append(accessLog.getHttpStatusCode())
            .append(LOG_SPLITTER)
            .append(Func.removeLineBreak(Func.toStrWithEmpty(accessLog.getRequestData(), StringPool.DASH)));

    // 个性化信息，拼接为key=value的格式
    Map<String, String> context = accessLog.getContext();
    if (context != null) {
      for (Map.Entry<String, String> entry : context.entrySet()) {
        logBuilder.append(LOG_SPLITTER).append(entry.getKey()).append('=').append(entry.getValue());
      }
    }

    return logBuilder.toString();
  }

  /**
   * 是否跳过请求数据的记录
   */
  private boolean isSkipRequestData(T request) {
    HandlerMethod handlerMethod = getHandlerMethod(request);
    return handlerMethod == null;
  }

  /**
   * 初始化access log共性参数
   */
  private void initAccessLog(T request) {
    accessLog = new AccessLog();
    accessLog.setCreateTime(LocalDateTime.now());
    accessLog.setTraceId(TraceContext.traceId());
    // 默认为SUCCESS
    accessLog.setResult(AccessLog.ResultType.SUCCESS);
    accessLog.setHttpMethod(getHttpMethod(request));
    accessLog.setClientIp(getClientIP(request));
    accessLog.setRequestUri(getRequestURI(request));
  }
}
