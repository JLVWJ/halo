package com.lvwj.halo.common.validation.constraints;

import com.lvwj.halo.common.constants.ValidateConstant;
import com.lvwj.halo.common.validation.validators.range.RangeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 范围区间校验，左区间不能大于右区间
 *
 * @author lvweijie
 * @date 2023/11/10 17:51
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = {RangeValidator.class})
public @interface Range {

  String message() default "数据范围异常：{from} 大于 {to}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
