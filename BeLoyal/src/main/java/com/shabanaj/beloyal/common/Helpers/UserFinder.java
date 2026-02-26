package com.shabanaj.beloyal.common.Helpers;

import com.shabanaj.beloyal.common.Exception.UserNotFound;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFinder {
    private final UserRepository userRepository;
    private final Logger logger= LoggerFactory.getLogger(UserFinder.class);
    public User findByIdOrThrows(Long id) {
        Optional<User> user= userRepository.findById(id);

        if(user.isEmpty()){
            throw new UserNotFound();
        }

        return user.get();
    }

    public User findByEmailOrThrows(String email) {
        Optional<User> user= userRepository.findUserByEmailIgnoreCase(email.trim().toLowerCase());

        if(user.isEmpty()){
            throw new UserNotFound();
        }

        return user.get();
    }

    public User findByEmailOrNull(String email) {
        logger.info("Inside findByEmailOrNull");
        Optional<User> user= userRepository.findUserByEmailIgnoreCase(email.trim().toLowerCase());
        logger.info("After query: Result: "+ user.isPresent());
        return user.orElse(null);
    }
}
