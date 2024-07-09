package com.lvwj.halo.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lvwj.halo.common.models.entity.IEntity;
import lombok.*;
import org.javers.core.metamodel.annotation.Id;

import java.io.Serializable;

/**
 * 数据实体(PO)
 *
 * @author lvwj
 * @date 2022-12-14 13:54
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Entity<ID extends Serializable> implements IEntity<ID> {

  @Id
  @TableId(value = "id", type = IdType.INPUT)
  private ID id;
}
