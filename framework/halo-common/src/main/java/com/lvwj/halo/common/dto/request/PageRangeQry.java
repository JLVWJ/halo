package com.lvwj.halo.common.dto.request;

import com.lvwj.halo.common.validation.constraints.Range;
import com.lvwj.halo.common.validation.validators.range.DateRange;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel("范围查询请求参数")
public abstract class PageRangeQry extends PageSortQry {

  /**
   * 创建时间范围查询，@Range会校验开始时间必须小于结束时间
   */
  @Range
  @ApiModelProperty(value = "创建时间")
  private DateRange createTime;

  /**
   * 更新时间范围查询，@Range会校验开始时间必须小于结束时间
   */
  @Range
  @ApiModelProperty(value = "更新时间")
  private DateRange updateTime;
}
