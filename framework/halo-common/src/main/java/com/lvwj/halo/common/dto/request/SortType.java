package com.lvwj.halo.common.dto.request;

import com.lvwj.halo.common.enums.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum SortType implements IEnum<Integer> {

  ASC(1, "正序"), DESC(-1, "倒序");

  private final Integer code;
  private final String description;
}
