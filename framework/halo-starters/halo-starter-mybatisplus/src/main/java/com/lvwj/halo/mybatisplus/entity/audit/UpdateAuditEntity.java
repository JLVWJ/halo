package com.lvwj.halo.mybatisplus.entity.audit;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基类实体-创建/修改审计
 *
 * @author lvwj
 * @date 2022-12-14 13:57
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateAuditEntity<ID extends Serializable> extends CreatAuditEntity<ID> {

  @DiffIgnore
  @TableField(fill = FieldFill.INSERT_UPDATE)
  protected LocalDateTime updateTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Long updateBy;
}
