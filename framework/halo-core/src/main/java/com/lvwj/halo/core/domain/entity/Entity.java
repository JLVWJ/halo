package com.lvwj.halo.core.domain.entity;

import com.lvwj.halo.common.models.entity.IEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Entity<ID extends Serializable> implements IEntity<ID> {

  private ID id;

  private String createBy;
  private String updateBy;
  private String deleteBy;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
  private LocalDateTime deleteTime;
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
