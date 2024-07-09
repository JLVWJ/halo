package com.lvwj.halo.core.domain.entity;

import java.io.Serializable;

/**
 * 值对象接口 【必须加@EqualsAndHashCode 来实现equals 和 hashCode】
 */
public interface IValueObj extends Serializable {

  boolean equals(final Object o);

  int hashCode();
}
