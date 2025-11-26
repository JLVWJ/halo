package com.lvwj.halo.web.handler;


import com.lvwj.halo.common.dto.response.R;
import com.lvwj.halo.common.enums.BaseErrorEnum;
import com.lvwj.halo.common.exceptions.ArgumentException;
import com.lvwj.halo.common.exceptions.BusinessException;
import com.lvwj.halo.common.exceptions.ForbiddenException;
import com.lvwj.halo.common.exceptions.UnauthorizedException;
import com.lvwj.halo.common.utils.StringPool;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 全局异常统一处理
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-05 15:49
 */
@Order(-50)
@Slf4j
@ConditionalOnWebApplication(type = Type.SERVLET)
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> bizException(BusinessException ex) {
    log.warn("BusinessException:", ex);
    return R.fail(ex.getErrorEnum(), ex.getMessage(), ex.getArgs());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ArgumentException.class)
  public R<?> argumentException(ArgumentException ex) {
    log.warn("ArgumentException:", ex);
    return R.fail(ex.getErrorEnum(), ex.getMessage(), ex.getArgs());
  }

  @ExceptionHandler(ForbiddenException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public R<?> forbiddenException(ForbiddenException ex) {
    log.warn("ForbiddenException:", ex);
    return R.fail(ex.getErrorEnum(), ex.getMessage(), ex.getArgs());
  }

  @ExceptionHandler(UnauthorizedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public R<?> unauthorizedException(UnauthorizedException ex) {
    log.warn("UnauthorizedException:", ex);
    return R.fail(ex.getErrorEnum(), ex.getMessage(), ex.getArgs());
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public R<?> handleError(NoHandlerFoundException e) {
    log.warn("404-NoHandlerFoundException:", e);
    return R.fail(BaseErrorEnum.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
    log.warn("HttpMessageNotReadableException:", ex);
    return R.fail(BaseErrorEnum.PARAM_TYPE_ERROR, ex.getMessage());
  }

  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> bindException(BindException ex) {
    log.warn("BindException:", ex);
    try {
      String msg = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
      if (null != msg && !msg.isEmpty()) {
        return R.fail(BaseErrorEnum.PARAM_BIND_ERROR, msg);
      }
    } catch (Exception ee) {
      log.debug("获取异常描述失败", ee);
    }
    StringBuilder msg = new StringBuilder();
    List<FieldError> fieldErrors = ex.getFieldErrors();
    fieldErrors.forEach((oe) ->
            msg.append("参数:[").append(oe.getObjectName())
                    .append(".").append(oe.getField())
                    .append("]的传入值:[").append(oe.getRejectedValue()).append("]与预期的字段类型不匹配.")
    );
    return R.fail(BaseErrorEnum.PARAM_TYPE_ERROR, msg.toString());
  }


  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    log.warn("MethodArgumentTypeMismatchException:", ex);
    String msg = "参数：[" + ex.getName() + "]的传入值：[" + ex.getValue() +
            "]与预期的字段类型：[" + Objects.requireNonNull(ex.getRequiredType()).getName() + "]不匹配";
    return R.fail(BaseErrorEnum.PARAM_TYPE_ERROR, msg);
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> illegalStateException(IllegalStateException ex) {
    log.warn("IllegalStateException:", ex);
    return R.fail(BaseErrorEnum.PARAM_ILLEGAL_ERROR, ex.getMessage());
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
    log.warn("MissingServletRequestParameterException:", ex);
    return R.fail(BaseErrorEnum.PARAM_ILLEGAL_ERROR,
            "缺少必须的[" + ex.getParameterType() + "]类型的参数[" + ex.getParameterName() + "]");
  }

  @ExceptionHandler(NullPointerException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> nullPointerException(NullPointerException ex) {
    log.warn("NullPointerException:", ex);
    return R.fail(BaseErrorEnum.NULL_POINT_ERROR, ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> illegalArgumentException(IllegalArgumentException ex) {
    log.warn("IllegalArgumentException:", ex);
    return R.fail(BaseErrorEnum.PARAM_ILLEGAL_ERROR, ex.getMessage());
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
    log.warn("HttpMediaTypeNotSupportedException:", ex);
    MediaType contentType = ex.getContentType();
    if (contentType != null) {
      return R.fail(BaseErrorEnum.MEDIA_TYPE_NOT_SUPPORTED,
              "请求类型(Content-Type)[" + contentType + "] 与实际接口的请求类型不匹配");
    }
    return R.fail(BaseErrorEnum.MEDIA_TYPE_NOT_SUPPORTED, "无效的Content-Type类型");
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> missingServletRequestPartException(MissingServletRequestPartException ex) {
    log.warn("MissingServletRequestPartException:", ex);
    return R.fail(BaseErrorEnum.MULTIPART_PARAM_ERROR, ex.getMessage());
  }

  @ExceptionHandler(ServletException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> servletException(ServletException ex) {
    log.warn("ServletException:", ex);
    String msg = "UT010016: Not a multi part request";
    if (msg.equalsIgnoreCase(ex.getMessage())) {
      return R.fail(BaseErrorEnum.MULTIPART_PARAM_ERROR, msg);
    }
    return R.fail(BaseErrorEnum.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  @ExceptionHandler(MultipartException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> multipartException(MultipartException ex) {
    log.warn("MultipartException:", ex);
    return R.fail(BaseErrorEnum.MULTIPART_PARAM_ERROR, ex.getMessage());
  }

  /**
   * jsr 规范中的验证异常
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> constraintViolationException(ConstraintViolationException ex) {
    log.warn("ConstraintViolationException:", ex);
    Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
    String message = violations.stream().map(s -> s.getPropertyPath().toString() + StringPool.COLON + s.getMessage()).collect(Collectors.joining(StringPool.SEMICOLON));
    return R.fail(BaseErrorEnum.PARAM_VALID_ERROR, message);
  }

  /**
   * spring 封装的参数验证异常， 在controller中没有写result参数时，会进入
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
    log.warn("MethodArgumentNotValidException:", ex);
    return R.fail(BaseErrorEnum.PARAM_VALID_ERROR,
            ex.getBindingResult().getFieldErrors().stream().map(s -> s.getField() + StringPool.COLON + s.getDefaultMessage()).collect(Collectors.joining(StringPool.SEMICOLON)));
  }

  /**
   * 返回状态码:405
   */
  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
    log.warn("HttpRequestMethodNotSupportedException:", ex);
    return R.fail(BaseErrorEnum.METHOD_NOT_SUPPORTED, ex.getMessage());
  }

  @ExceptionHandler(SQLException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public R<?> sqlException(SQLException ex) {
    log.warn("SQLException:", ex);
    return R.fail(BaseErrorEnum.SQL_ERROR, ex.getMessage());
  }

  /**
   * 其他异常
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public R<?> otherException(Exception ex) {
    log.warn("Exception:", ex);
    if (ex.getCause() instanceof BusinessException cause) {
      return this.bizException(cause);
    }
    return R.fail(BaseErrorEnum.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  /**
   * 未知异常处理
   */
  @ExceptionHandler(value = Throwable.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public R<?> handleUnknownException(Throwable e) {
    log.warn("Throwable:", e);
    return R.fail(BaseErrorEnum.INTERNAL_SERVER_ERROR, e.getMessage());
  }
}
