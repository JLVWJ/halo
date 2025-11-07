package com.lvwj.halo.common.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lvwj.halo.common.constants.SystemConstant;
import com.lvwj.halo.common.enums.BaseErrorEnum;
import com.lvwj.halo.common.enums.IErrorEnum;
import com.lvwj.halo.common.exceptions.BusinessException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.beans.Transient;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 统一API响应结果封装
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-04 09:39
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class R<T> implements Serializable {

  private int code;

  private String message;

  private T data;

  @JsonIgnore
  private transient IErrorEnum errorCode;

  @JsonIgnore
  private transient Object[] args;

  protected R(IErrorEnum errorCode, Object[] args) {
    this(errorCode, null, errorCode.getDescription(), args);
  }

  protected R(IErrorEnum errorCode, String message, Object[] args) {
    this(errorCode, null, message, args);
  }

  protected R(IErrorEnum errorCode, T data) {
    this(errorCode, data, errorCode.getDescription(), null);
  }

  protected R(IErrorEnum errorCode, T data, String message, Object[] args) {
    this.errorCode = errorCode;
    this.code = errorCode.getCode();
    this.data = data;
    this.message = null != message && !message.isEmpty() ? message : errorCode.getDescription();
    this.args = args;
  }

  protected R(int code, T data, String message, Object[] args) {
    this.code = code;
    this.data = data;
    this.message = message;
    this.args = args;
  }

  public String getMessage() {
    return MessageFormat.format(message, args);
  }

  public String getMsg(){
    return getMessage();
  }

  public boolean isSuccess() {
    return BaseErrorEnum.SUCCESS.getCode() == code || code == 200;
  }

  @Transient
  @JsonIgnore
  public Object[] getArgs() {
    return args;
  }

  @Transient
  @JsonIgnore
  public IErrorEnum getErrorCode() {
    return errorCode;
  }

  /**
   * 获取数据(code!=200时抛异常)
   *
   * @return data
   */
  @Transient
  @JsonIgnore
  public T getOrThrow() {
    return getOrThrow(() -> null);
  }

  @Transient
  @JsonIgnore
  public T getOrThrow(Supplier<T> supplierIfNull) {
    return getOrThrow(true, () -> null);
  }

  /**
   * 获取数据(code!=200时不抛异常)
   *
   * @return data
   */
  @Transient
  @JsonIgnore
  public T getOrNull() {
    return getOrNull(() -> null);
  }

  @Transient
  @JsonIgnore
  public T getOrNull(Supplier<T> supplierIfNull) {
    return getOrThrow(false, supplierIfNull);
  }

  /**
   * 获取数据
   *
   * @param throwIfFailed  code!=200时:true 抛异常，false 不抛异常
   * @param supplierIfNull data=null时的处理
   * @return data
   */
  @Transient
  @JsonIgnore
  private T getOrThrow(boolean throwIfFailed, Supplier<T> supplierIfNull) {
    if (!isSuccess() && throwIfFailed) {
      throw new BusinessException(BaseErrorEnum.RPC_FAILED, getMessage());
    }
    return Optional.ofNullable(getData()).orElseGet(supplierIfNull);
  }


  public static R<Boolean> success() {
    return success(Boolean.TRUE, BaseErrorEnum.SUCCESS.getDescription());
  }

  /**
   * 返回R
   *
   * @param data 数据
   * @param <T>  T 泛型标记
   * @return R
   */
  public static <T> R<T> success(T data) {
    return success(data, BaseErrorEnum.SUCCESS.getDescription());
  }

  /**
   * 返回R
   *
   * @param data 数据
   * @param msg  消息
   * @param <T>  T 泛型标记
   * @return R
   */
  public static <T> R<T> success(T data, String msg) {
    return new R<>(BaseErrorEnum.SUCCESS, data, data == null ? "暂无承载数据" : msg, null);
  }

  /**
   * 返回R
   *
   * @param msg 消息
   * @param <T> T 泛型标记
   * @return R
   */
  public static <T> R<T> fail(String msg) {
    return new R<>(BaseErrorEnum.FAILURE, msg, null);
  }

  public static <T> R<T> fail() {
    return new R<>(BaseErrorEnum.FAILURE, SystemConstant.ERROR, null);
  }

  public static <T> R<T> fail(BusinessException exception) {
    if (null != exception.getErrorEnum()) {
      return new R<>(exception.getErrorEnum(), null, exception.getMessage(), exception.getArgs());
    }
    return new R<>(exception.getCode(), null, exception.getMessage(), exception.getArgs());
  }


  /**
   * 返回R
   *
   * @param code 状态码
   * @param msg  消息
   * @param <T>  T 泛型标记
   * @return R
   */
  public static <T> R<T> fail(int code, String msg) {
    return new R<>(code, null, msg, null);
  }

  public static <T> R<T> fail(int code, String msg, Object... args) {
    return new R<>(code, null, msg, args);
  }

  /**
   * 返回R
   *
   * @param errorCode 业务代码
   * @param <T>       T 泛型标记
   * @return R
   */
  public static <T> R<T> fail(final IErrorEnum errorCode) {
    return new R<>(errorCode, null);
  }

  public static <T> R<T> fail(final IErrorEnum errorCode, final String msg, Object... args) {
    return new R<>(errorCode, msg, args);
  }

  public static <T> R<T> fail(final IErrorEnum errorCode, Object... args) {
    return new R<>(errorCode, args);
  }
}
