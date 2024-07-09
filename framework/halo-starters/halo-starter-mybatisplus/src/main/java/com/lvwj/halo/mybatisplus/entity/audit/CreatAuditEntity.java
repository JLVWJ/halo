package com.lvwj.halo.mybatisplus.entity.audit;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.lvwj.halo.mybatisplus.entity.Entity;
import lombok.*;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基类实体-创建审计
 *
 * @author lvwj
 * @date 2022-12-14 13:57
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreatAuditEntity<ID extends Serializable> extends Entity<ID> {

  @DiffIgnore
  @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
  private Long createBy;
}
