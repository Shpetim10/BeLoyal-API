package com.shabanaj.beloyal.common.Validation.Validator;

import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.common.Validation.Annotation.UniqueEmailOnCreate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailOnCreateValidator implements ConstraintValidator<UniqueEmailOnCreate, String> {

    private final UserRepository userRepository;

    public UniqueEmailOnCreateValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true; // let @NotBlank handle this
        }
        return userRepository.findUserByEmail(email).isEmpty();
    }
}
