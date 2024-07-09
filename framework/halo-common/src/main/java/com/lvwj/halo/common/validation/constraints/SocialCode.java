package com.lvwj.halo.common.validation.constraints;

import com.lvwj.halo.common.constants.ValidateConstant;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 判断值是否统一社会信用码
 *
 * @author lvweijie
 * @since 2021-07-20
 */
@Documented
@Constraint(validatedBy = {})
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(SocialCode.List.class)
@Pattern(regexp = ValidateConstant.REG_EXP_SOCIAL_CODE)
@ReportAsSingleViolation
public @interface SocialCode {

  String message() default ValidateConstant.MSG_SOCIAL_CODE;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /**
   * Defines several {@code @SocialCode} annotations on the same element.
   */
  @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
  @Retention(RUNTIME)
  @Documented
  @interface List {

    SocialCode[] value();
  }
}
