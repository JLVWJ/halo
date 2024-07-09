package com.lvwj.halo.common.validation.validators.range;

import java.io.Serializable;

/**
 * 范围校验通用接口
 *
 * @author lvweijie
 * @date 2023/11/10 17:44
 */
public interface IRange<T> extends Serializable {

  boolean valid();

  T getFrom();

  T getTo();
}
