package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Repository.UserRepository;
import com.shabanaj.beloyal.Service.ValidationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ValidationServiceImpl implements ValidationService {
    private final UserRepository userRepository;

    public ValidationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;

        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

        return email.matches(emailRegex);
    }

    @Override
    public boolean isUniqueEmail(Long userId,String email){
        Optional<User> user = userRepository.findUserByEmail(email);

        return user.isEmpty() || user.get().getId().equals(userId) ;
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) return false;
        // Accepts digits, optional +, spaces, dashes, and parentheses
        String phoneRegex = "^\\+?[0-9\\s\\-()]{7,15}$";
        return phoneNumber.matches(phoneRegex);
    }

    @Override
    public boolean isValidPassword(String password) {
        if (password == null || password.isBlank()) return false;
        // Minimum 8 characters, at least one uppercase, one lowercase, one digit, one special character
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordRegex);
    }

    @Override
    public boolean isUniqueUsername(Long userId, String username){
        if(username == null || username.isBlank()) return false;
        Optional<User> user = userRepository.findUserByUsername(username);

        return user.isEmpty() || user.get().getId().equals(userId);
    }

    @Override
    public boolean isUniquePhoneNumber(Long userId, String phoneNumber) {
        Optional<User> user = userRepository.findUserByPhoneNumber(phoneNumber);

        return user.isEmpty() || user.get().getId().equals(userId);
    }
}
