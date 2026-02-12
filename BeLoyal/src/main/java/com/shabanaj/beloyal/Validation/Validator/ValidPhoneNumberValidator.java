package com.shabanaj.beloyal.Validation.Validator;

import com.shabanaj.beloyal.Validation.ValidationService;
import com.shabanaj.beloyal.Validation.Annotation.ValidPhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ValidPhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private final ValidationService validationService;

    public ValidPhoneNumberValidator(ValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return validationService.isValidPhoneNumber(value);
    }
}