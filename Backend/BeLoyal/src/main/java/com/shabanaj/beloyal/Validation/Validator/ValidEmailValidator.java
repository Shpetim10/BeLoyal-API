package com.shabanaj.beloyal.Validation.Validator;

import com.shabanaj.beloyal.Service.ValidationService;
import com.shabanaj.beloyal.Validation.Annotation.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {

    private final ValidationService validationService;

    public ValidEmailValidator(ValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Let @NotNull / @NotBlank handle null/empty if needed
        if (value == null || value.isBlank()) {
            return true;
        }
        return validationService.isValidEmail(value);
    }
}