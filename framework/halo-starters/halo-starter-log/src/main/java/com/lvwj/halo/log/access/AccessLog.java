package com.lvwj.halo.log.access;

import com.lvwj.halo.log.constant.ResultType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author lvweijie
 * @date 2024年07月06日 10:05
 */
@Getter
@Setter
public class AccessLog {

  // 创建时间
  private LocalDateTime createTime;

  // 链路追踪id
  private String traceId;

  // 请求结果
  private ResultType result;

  // 处理类和方法
  private String action;

  // 耗时
  private Long elapsed;

  // 调用端IP
  private String clientIp;

  // 请求地址
  private String requestUri;

  // HTTP方法
  private String httpMethod;

  // HTTP状态码
  private Integer httpStatusCode;

  // 请求数据
  private String requestData;

  // 上下文信息，用于扩展存储
  private Map<String, String> context;
}
