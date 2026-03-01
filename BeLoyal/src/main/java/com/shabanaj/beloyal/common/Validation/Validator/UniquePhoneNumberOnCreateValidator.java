package com.shabanaj.beloyal.common.Validation.Validator;

import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.common.Validation.Annotation.UniquePhoneNumberOnCreate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniquePhoneNumberOnCreateValidator implements ConstraintValidator<UniquePhoneNumberOnCreate, String> {

    private final UserRepository userRepository;

    public UniquePhoneNumberOnCreateValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return true;
        }
        return userRepository.findUserByPhoneNumber(phoneNumber).isEmpty();
    }
}