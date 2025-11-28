package com.shabanaj.beloyal.Validation.Validator;

import com.shabanaj.beloyal.Repository.UserRepository;
import com.shabanaj.beloyal.Validation.Annotation.UniqueUsernameOnCreate;
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
