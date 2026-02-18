package com.shabanaj.beloyal.common.Validation.Validator;

import com.shabanaj.beloyal.user.repository.UserRepository;
import com.shabanaj.beloyal.common.Validation.Annotation.UniqueUsernameOnCreate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueUsernameOnCreateValidator implements ConstraintValidator<UniqueUsernameOnCreate, String> {

    private final UserRepository userRepository;

    public UniqueUsernameOnCreateValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.isBlank()) {
            return true;
        }
        return userRepository.findUserByUsername(username).isEmpty();
    }
}
