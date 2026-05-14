package com.shabanaj.beloyal.common.Validation.Validator;

import com.shabanaj.beloyal.common.Validation.Annotation.UniqueUsernameOnUpdate;
import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UniqueUsernameOnUpdateValidator implements ConstraintValidator<UniqueUsernameOnUpdate, String> {

    private final UserRepository userRepository;

    public UniqueUsernameOnUpdateValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String usernameValue, ConstraintValidatorContext context) {
        if (usernameValue == null || usernameValue.isBlank()) {
            return true;
        }

        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }

        var existingUser = userRepository.findUserByUsername(usernameValue);
        if (existingUser.isEmpty()) {
            return true;
        }

        return existingUser.get().getId().equals(currentUserId);
    }

    private Long getCurrentUserId() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
                return principal.getId();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
