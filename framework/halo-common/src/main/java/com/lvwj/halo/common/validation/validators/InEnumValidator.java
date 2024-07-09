package com.lvwj.halo.common.validation.validators;

import com.lvwj.halo.common.enums.IEnum;
import com.lvwj.halo.common.validation.constraints.InEnum;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class InEnumValidator implements ConstraintValidator<InEnum, Integer> {
    private Set<?> codeSet;

    @Override
    public void initialize(InEnum anEnum) {
        IEnum<?>[] enums = anEnum.value().getEnumConstants();
        codeSet = Arrays.stream(enums).map(IEnum::getCode).collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        //设置message参数
        context.unwrap(HibernateConstraintValidatorContext.class)
                .addMessageParameter("param", value.toString())
                .addMessageParameter("codes", codeSet.toString());
        //判断是否在枚举中
        return codeSet.contains(value);
    }
}
