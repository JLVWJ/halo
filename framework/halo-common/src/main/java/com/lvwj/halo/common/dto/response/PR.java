package com.lvwj.halo.common.dto.response;

import com.lvwj.halo.common.enums.BaseErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页统一返回结果
 *
 * @author lvwj
 * @date 2023-01-04 11:23
 */
public class PR<T> extends R<PR.PD<T>> {

  protected PR(Integer total, Integer page, Integer pageSize, List<T> data) {
    super(BaseErrorEnum.SUCCESS, new PD<>(total, page, pageSize, data));
  }

  public static <T> PR<T> success(Integer total, Integer page, Integer pageSize, List<T> data) {
    return new PR<>(total, page, pageSize, data);
  }

  public static <T> PR<T> success(Integer page, Integer pageSize) {
    return PR.success(0, page, pageSize, new ArrayList<>());
  }

  @Data
  @AllArgsConstructor
  public static class PD<D> implements Serializable {

    /**
     * 总记录数
     */
    private Integer total;

    /**
     * 当前页码
     */
    private Integer pageNo;

    /**
     * 每页记录数
     */
    private Integer pageSize;

    /**
     * 分页数据
     */
    private List<D> list;
  }
}
