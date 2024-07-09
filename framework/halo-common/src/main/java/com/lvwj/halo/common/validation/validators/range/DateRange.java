package com.lvwj.halo.common.validation.validators.range;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DateRange implements IRange<Date>{

  private Date from;

  private Date to;

  @Override
  public boolean valid() {
    if (from == null || to == null) {
      return true;
    }
    return !from.after(to);
  }
}
