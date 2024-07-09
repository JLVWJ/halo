package com.lvwj.halo.common.dto.request;

import com.lvwj.halo.common.validation.constraints.InEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel("分页排序请求参数")
public abstract class PageSortQry extends PageQry {

  /**
   * 排序
   */
  @ApiModelProperty(value = "排序")
  private List<@Valid Sort> sorts;

  @Data
  @Accessors(chain = true)
  @ApiModel("排序参数")
  public static class Sort implements Serializable {

    @NotBlank
    @ApiModelProperty(value = "排序参数")
    private String sortBy;

    @NotNull
    @InEnum(value = SortType.class)
    @ApiModelProperty(value = "排序类型(1:正序，-1:倒序)")
    private Integer sortType;
  }
}
