package com.lvwj.halo.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.*;
import org.javers.core.metamodel.annotation.DiffIgnore;

/**
 *
 * @author lvweijie
 * @date 2023年11月10日 17:17
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DeleteEntity extends BaseEntity{

    @DiffIgnore
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "is_delete", fill = FieldFill.INSERT_UPDATE)
    private Integer isDelete;
}
