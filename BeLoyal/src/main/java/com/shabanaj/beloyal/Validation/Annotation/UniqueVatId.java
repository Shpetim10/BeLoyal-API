package com.shabanaj.beloyal.Validation.Annotation;

import com.shabanaj.beloyal.Validation.Validator.UniqueUsernameOnCreateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUsernameOnCreateValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueVatId {
    String message() default "A businesss with this VatId already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
