package com.shabanaj.beloyal.common.Helpers;

import com.shabanaj.beloyal.common.Exception.UserNotFound;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFinder {
    private final UserService userService;

    public User findByEmail(String email) {
        Optional<User> user= userService.getUserByEmail(email);

        if(!user.isPresent()){
            throw new UserNotFound();
        }

        return user.get();
    }
}
