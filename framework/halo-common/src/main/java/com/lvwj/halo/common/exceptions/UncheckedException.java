package com.lvwj.halo.common.exceptions;

import com.lvwj.halo.common.enums.IErrorEnum;
import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;

/**
 * 非运行期异常基类，所有自定义非运行时异常继承该类
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-05 11:30
 */
@Getter
public class UncheckedException extends RuntimeException {

  /**
   * 具体异常码
   */
  private int code;
  /**
   * 异常信息
   */
  @Setter
  private String message;
  /**
   * 异常码
   */
  private IErrorEnum errorEnum;
  /**
   * args
   */
  private Object[] args;

  public UncheckedException(Throwable cause) {
    super(cause);
  }

  public UncheckedException(final int code, Throwable cause) {
    super(cause);
    this.code = code;
    this.message = cause.getLocalizedMessage();
  }

  public UncheckedException(final int code, final String message) {
    super(message);
    this.code = code;
    this.message = message;
  }

  public UncheckedException(final int code, final String message, Throwable cause) {
    super(cause);
    this.code = code;
    this.message = null == message || message.isEmpty() ? cause.getLocalizedMessage() : message;
  }

  public UncheckedException(final int code, final String message, Object... args) {
    super(MessageFormat.format(message, args));
    this.code = code;
    this.message = MessageFormat.format(message, args);
    this.args = args;
  }

  public UncheckedException(final IErrorEnum errorEnum, Throwable cause) {
    super(cause);
    this.errorEnum = errorEnum;
    this.code = errorEnum.getCode();
    this.message = errorEnum.getDescription();
  }

  public UncheckedException(final IErrorEnum errorEnum) {
    this(errorEnum, "");
  }

  public UncheckedException(final IErrorEnum errorEnum, Object... args) {
    super(MessageFormat.format(errorEnum.getDescription(), args));
    this.errorEnum = errorEnum;
    this.code = errorEnum.getCode();
    this.message = MessageFormat.format(errorEnum.getDescription(), args);
    this.args = args;
  }
}
