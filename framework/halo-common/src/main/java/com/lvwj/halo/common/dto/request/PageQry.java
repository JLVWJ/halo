package com.lvwj.halo.common.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("分页请求参数")
public abstract class PageQry implements Serializable {

  /**
   * 当前页码
   */
  @NotNull
  @Positive
  @ApiModelProperty(value = "当前页码", required = true)
  private Long pageNo = 1L;

  /**
   * 每页大小
   */
  @NotNull
  @Positive
  @ApiModelProperty(value = "每页大小", required = true)
  private Long pageSize = 20L;
}
