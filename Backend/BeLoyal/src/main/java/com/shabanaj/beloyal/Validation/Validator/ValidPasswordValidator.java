package com.shabanaj.beloyal.Validation.Validator;

import com.shabanaj.beloyal.Service.ValidationService;
import com.shabanaj.beloyal.Validation.Annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private final ValidationService validationService;

    public ValidPasswordValidator(ValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // use @NotBlank in DTO if you require it
        }
        return validationService.isValidPassword(value);
    }
}