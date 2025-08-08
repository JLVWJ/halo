package com.lvwj.halo.common.dto.request;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public abstract class PageTimeQry extends PageSortQry {

  private LocalDateTime createTimeStart;
  private LocalDateTime createTimeEnd;

  private LocalDateTime updateTimeStart;
  private LocalDateTime updateTimeEnd;
}
