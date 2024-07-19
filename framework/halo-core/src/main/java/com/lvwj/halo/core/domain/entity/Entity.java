package com.lvwj.halo.core.domain.entity;

import com.lvwj.halo.common.models.entity.IEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Entity<ID extends Serializable> implements IEntity<ID> {

  private ID id;

  //暂存PO的字段数据，无业务用途
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
  /**
   * 删除人
   */
  private String deleteBy;
  /**
   * 删除时间
   */
  private LocalDateTime deleteTime;
  /**
   * 是否删除
   */
  private Integer isDelete;


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Entity<?> that = (Entity<?>) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
