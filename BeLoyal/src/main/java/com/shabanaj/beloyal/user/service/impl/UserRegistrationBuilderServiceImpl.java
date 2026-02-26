package com.shabanaj.beloyal.user.service.impl;

import com.shabanaj.beloyal.common.Exception.TCNotAcceptedException;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.registration.dto.RegisterUserDto;
import com.shabanaj.beloyal.user.service.UserRegistrationBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserRegistrationBuilderServiceImpl implements UserRegistrationBuilderService {
    private final PasswordEncoder passwordEncoder;

    @Override
    public User buildUserFromRegistration(RegisterUserDto dto) {
        if(!dto.isAcceptedTc())
            throw new TCNotAcceptedException();

        //Register user first
        User user= new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        user.setAcceptedTcVersion(dto.getAcceptedTcVersion());
        user.setAcceptedTcAt(LocalDateTime.now());

        return user;
    }
}
