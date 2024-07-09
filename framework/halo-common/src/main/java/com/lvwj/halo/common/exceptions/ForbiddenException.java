package com.lvwj.halo.common.exceptions;

import com.lvwj.halo.common.enums.BaseErrorEnum;

/**
 * 403  禁止访问
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-05 11:55
 */
public class ForbiddenException extends UncheckedException {

  public ForbiddenException() {
    super(BaseErrorEnum.FORBIDDEN);
  }

  public ForbiddenException(Throwable cause) {
    super(BaseErrorEnum.FORBIDDEN, cause);
  }

  @Override
  public String toString() {
    return "ForbiddenException [message=" + getMessage() + ", code=" + getCode() + "]";
  }
}
