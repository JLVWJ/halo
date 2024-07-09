package com.lvwj.halo.common.exceptions;


import com.lvwj.halo.common.enums.IErrorEnum;

/**
 * 业务异常
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-03 16:21
 */
public class BusinessException extends UncheckedException {

  public BusinessException(IErrorEnum resultCode) {
    super(resultCode);
  }

  public BusinessException(IErrorEnum resultCode, Throwable cause) {
    super(resultCode, cause);
  }

  public BusinessException(IErrorEnum resultCode, Object... args) {
    super(resultCode, args);
  }
}
