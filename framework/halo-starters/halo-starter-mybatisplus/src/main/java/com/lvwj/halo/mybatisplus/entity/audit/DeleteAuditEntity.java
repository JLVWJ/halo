package com.lvwj.halo.mybatisplus.entity.audit;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基类实体-创建/修改/删除审计
 *
 * @author lvwj
 * @date 2022-12-14 13:57
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeleteAuditEntity<ID extends Serializable> extends UpdateAuditEntity<ID> {

  /**
   * 逻辑删除
   */
  //@DiffIgnore
  @TableLogic(value = "0", delval = "1")
  @TableField(value = "is_delete", fill = FieldFill.INSERT_UPDATE)
  private Integer isDelete;

  /**
   * 删除时间
   */
  //@DiffIgnore
  @TableField(fill = FieldFill.UPDATE)
  protected LocalDateTime deleteTime;

  /**
   * 删除人
   */
  @TableField(fill = FieldFill.UPDATE)
  private String deleteBy;
}
