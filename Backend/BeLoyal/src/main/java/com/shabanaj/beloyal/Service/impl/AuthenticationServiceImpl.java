package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Dto.Login.LoginRequest;
import com.shabanaj.beloyal.Dto.Login.LoginResponse;
import com.shabanaj.beloyal.Dto.RegisterUserDto;
import com.shabanaj.beloyal.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Exception.TokenIsNotValidException;
import com.shabanaj.beloyal.Repository.UserRepository;
import com.shabanaj.beloyal.Repository.VerificationTokenRepository;
import com.shabanaj.beloyal.Security.CustomUserDetailsService;
import com.shabanaj.beloyal.Security.JwtService;
import com.shabanaj.beloyal.Service.AuthenticationService;
import com.shabanaj.beloyal.Service.EmailService;
import com.shabanaj.beloyal.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    public AuthenticationServiceImpl(UserService userService, VerificationTokenRepository verificationTokenRepository, EmailService emailService, UserRepository userRepository, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtService jwtService) {
        this.userService = userService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
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

    @Override
    public LoginResponse loginUser(LoginRequest request) {
        Authentication auth= new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );
        authenticationManager.authenticate(auth);

        UserDetails userDetails= customUserDetailsService.loadUserByUsername(request.getEmail());
        String jwt= jwtService.generateToken(userDetails);

        User user= userRepository.findUserByEmail(request.getEmail())
                .orElseThrow();

        LoginResponse response=new LoginResponse();
        response.setToken(jwt);
        response.setRoles(user.getRoles());
        return response;
    }
}
