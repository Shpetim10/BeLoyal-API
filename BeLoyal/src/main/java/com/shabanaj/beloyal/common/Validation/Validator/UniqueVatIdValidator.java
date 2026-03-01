package com.shabanaj.beloyal.common.Validation.Validator;

import com.shabanaj.beloyal.features.business.repository.BusinessRepository;
import com.shabanaj.beloyal.common.Validation.Annotation.UniqueVatId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueVatIdValidator implements ConstraintValidator<UniqueVatId, String> {
    private final BusinessRepository businessRepository;

    public UniqueVatIdValidator(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.length() == 0) {
            return true;
        }

        return businessRepository.findBusinessByVatId(s.trim()).isEmpty();
    }
}
