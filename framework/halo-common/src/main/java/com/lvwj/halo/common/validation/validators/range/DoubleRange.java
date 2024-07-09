package com.lvwj.halo.common.validation.validators.range;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DoubleRange implements IRange<Double> {

  private Double from;

  private Double to;

  @Override
  public boolean valid() {
    if (from == null || to == null) {
      return true;
    }
    return from.compareTo(to) <= 0;
  }
}
