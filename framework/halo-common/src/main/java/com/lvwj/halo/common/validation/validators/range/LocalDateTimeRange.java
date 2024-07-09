package com.lvwj.halo.common.validation.validators.range;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocalDateTimeRange implements IRange<LocalDateTime> {

  private LocalDateTime from;

  private LocalDateTime to;

  @Override
  public boolean valid() {
    if (from == null || to == null) {
      return true;
    }
    return !from.isAfter(to);
  }
}
