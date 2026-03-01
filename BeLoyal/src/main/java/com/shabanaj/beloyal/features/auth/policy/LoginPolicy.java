package com.shabanaj.beloyal.features.auth.policy;

import com.shabanaj.beloyal.common.Exception.UserIsDisabledException;
import com.shabanaj.beloyal.common.Exception.UserIsLockedException;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.UserStatus;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginPolicy {
    private final UserRepository userRepository;
    private final Clock clock;

    public void validateOrThrow(User user) {
        LocalDateTime now = LocalDateTime.now(clock);

        if (user.isLockActive(now)) {
            throw new UserIsLockedException();
        }

        if (user.getStatus() == UserStatus.LOCKED) {
            // lock expired
            user.unlock();
            userRepository.save(user);
        }

        if (user.getStatus() != UserStatus.ENABLED &&
                user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new UserIsDisabledException();
        }
    }
}
