package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Dto.RegisterUserDto;
import com.shabanaj.beloyal.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Service.AuthenticationService;
import com.shabanaj.beloyal.Service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;

    public AuthenticationServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void registerUser(RegisterUserDto dto) {
        //Register user first
        User user= new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(dto.getPassword());

        userService.createUser(user);

        //Create verification token and send it to user's email
        String token= UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
    }
}
