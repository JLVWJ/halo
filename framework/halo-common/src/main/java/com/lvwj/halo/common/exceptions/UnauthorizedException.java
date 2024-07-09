package com.lvwj.halo.common.exceptions;

import com.lvwj.halo.common.enums.BaseErrorEnum;

/**
 * 401 未认证/未登录
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-05 11:42
 */
public class UnauthorizedException extends UncheckedException {

  public UnauthorizedException() {
    super(BaseErrorEnum.UNAUTHORIZED);
  }

  public UnauthorizedException(Throwable cause) {
    super(BaseErrorEnum.UNAUTHORIZED, cause);
  }

  @Override
  public String toString() {
    return "UnauthorizedException [message=" + getMessage() + ", code=" + getCode() + "]";
  }
}
