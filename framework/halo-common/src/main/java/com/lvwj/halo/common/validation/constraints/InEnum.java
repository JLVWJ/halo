package com.lvwj.halo.common.validation.constraints;

import com.lvwj.halo.common.constants.ValidateConstant;
import com.lvwj.halo.common.enums.IEnum;
import com.lvwj.halo.common.validation.validators.InEnumValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 判断code是否在枚举范围内
 *
 * @author lvweijie
 * @since 2021-07-20
 */
@Documented
@Constraint(validatedBy = InEnumValidator.class)
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(InEnum.List.class)
@ReportAsSingleViolation
public @interface InEnum {
    /**
     * 枚举类型 需实现IEnum<?>
     */
    Class<? extends IEnum<?>> value();

    String message() default "{param}不能超出枚举范围{codes}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @InEnum} annotations on the same element.
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        InEnum[] value();
    }
}
