package com.shabanaj.beloyal.common.Validation.Annotation;

import com.shabanaj.beloyal.common.Validation.Validator.ValidPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidPasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "Invalid password format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
