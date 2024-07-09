package com.lvwj.halo.common.utils;

import com.lvwj.halo.common.enums.BaseErrorEnum;
import com.lvwj.halo.common.exceptions.BusinessException;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Optional;

/**
 * @author huxl
 */
public class ValidationUtil {

    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();

    public static void validate(Object target) {
        Validator validator = VALIDATOR_FACTORY.getValidator();
        Optional.ofNullable(validator.validate(target)).filter(CollectionUtil::isNotEmpty).ifPresent(it -> {
            throw new BusinessException(BaseErrorEnum.PARAM_VALID_ERROR);
        });
    }

}
