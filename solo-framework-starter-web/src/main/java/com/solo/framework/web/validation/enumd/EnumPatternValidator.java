package com.solo.framework.web.validation.enumd;

import com.solo.framework.common.util.ReflectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * 自定义枚举校验注解实现
 */
public class EnumPatternValidator implements ConstraintValidator<EnumPattern, Object> {

    private EnumPattern annotation;;

    @Override
    public void initialize(EnumPattern annotation) {
        ConstraintValidator.super.initialize(annotation);
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        // 为空默认校验成功，如需校验为空，请设置@NotBlank注解
        if (Objects.isNull(value)) {
            return true;
        }

        return checkEnumValue(value);
    }

    private boolean checkEnumValue(Object value) {
        String fieldName = annotation.fieldName();
        for (Object e : annotation.type().getEnumConstants()) {
            if (value.equals(ReflectionUtils.invokeGetter(e, fieldName))) {
                return true;
            }
        }
        return false;
    }

}
