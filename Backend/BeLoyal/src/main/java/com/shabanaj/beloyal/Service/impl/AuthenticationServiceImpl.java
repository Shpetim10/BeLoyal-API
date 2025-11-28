package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Dto.RegisterUserDto;
import com.shabanaj.beloyal.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Exception.TokenIsNotValidException;
import com.shabanaj.beloyal.Repository.UserRepository;
import com.shabanaj.beloyal.Repository.VerificationTokenRepository;
import com.shabanaj.beloyal.Service.AuthenticationService;
import com.shabanaj.beloyal.Service.EmailService;
import com.shabanaj.beloyal.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public AuthenticationServiceImpl(UserService userService, VerificationTokenRepository verificationTokenRepository, EmailService emailService, UserRepository userRepository) {
        this.userService = userService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void registerUser(RegisterUserDto dto) {
        //Register user first
        User user= new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(dto.getPassword());
        user.setRoles(dto.getRoles());

        User savedUser= userService.createUser(user);

        //Create verification token and send it to user's email
        String token= UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);

        emailService.sendActivationEmail(savedUser, token);
    }

    @Transactional
    public void activateUser(String token){
        EmailVerificationToken activationToken= verificationTokenRepository.findByToken(token)
                .orElseThrow(()->new TokenIsNotValidException("Token is not found!"));

        if(activationToken.isUsed()){
            throw new TokenIsNotValidException();
        }

        if(activationToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new TokenIsNotValidException();
        }

        User currentUser= activationToken.getUser();
        currentUser.setEnabled(true);
        currentUser.setEmailVerified(true);
        currentUser.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(currentUser);

        activationToken.setUsed(true);
        verificationTokenRepository.save(activationToken);
    }
}
