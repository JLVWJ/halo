package com.lvwj.halo.common.exceptions;


import com.lvwj.halo.common.enums.BaseErrorEnum;

/**
 * 业务参数异常 用于在业务中，检测到非法参数时，进行抛出的异常。
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-05 15:47
 */
public class ArgumentException extends UncheckedException {

  public ArgumentException(Throwable cause) {
    super(BaseErrorEnum.PARAM_VALID_ERROR,cause);
  }

  public ArgumentException(String message) {
    super(BaseErrorEnum.PARAM_VALID_ERROR.getCode(), message);
  }

  public ArgumentException(String message, Throwable cause) {
    super(BaseErrorEnum.PARAM_VALID_ERROR.getCode(), message, cause);
  }

  public ArgumentException(String message, Object... args) {
    super(BaseErrorEnum.PARAM_VALID_ERROR.getCode(), message, args);
  }

  @Override
  public String toString() {
    return "ArgumentException [message=" + getMessage() + ", code=" + getCode() + "]";
  }
}
