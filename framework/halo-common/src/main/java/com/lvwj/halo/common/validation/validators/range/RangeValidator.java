package com.lvwj.halo.common.validation.validators.range;


import com.lvwj.halo.common.validation.constraints.Range;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class RangeValidator implements ConstraintValidator<Range, IRange<?>> {

  @Override
  public boolean isValid(IRange<?> value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    boolean valid = value.valid();
    if (!valid) {
      //设置message参数
      context.unwrap(HibernateConstraintValidatorContext.class)
              .addMessageParameter("from", value.getFrom().toString())
              .addMessageParameter("to", value.getTo().toString());
    }
    return valid;
  }
}
