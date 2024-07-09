package com.lvwj.halo.common.validation.validators.range;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BigDecimalRange implements IRange<BigDecimal> {

  private BigDecimal from;

  private BigDecimal to;

  @Override
  public boolean valid() {
    if (from == null || to == null) {
      return true;
    }
    return from.compareTo(to) <= 0;
  }
}
