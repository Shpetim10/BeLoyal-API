package com.shabanaj.beloyal.common.Validation.Annotation;

import com.shabanaj.beloyal.common.Validation.Validator.UniquePhoneNumberOnCreateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniquePhoneNumberOnCreateValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniquePhoneNumberOnCreate {

    String message() default "Phone number already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
