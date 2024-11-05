package com.lvwj.halo.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.time.LocalDateTime;

/**
 * 数据实体基类
 *
 * @author lvweijie
 * @date 2023年11月03日 16:19
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BaseEntity extends Entity<Long> {

    @DiffIgnore
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;
}
