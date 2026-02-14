package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Dto.Login.LoginRequest;
import com.shabanaj.beloyal.Dto.Login.LoginResponse;
import com.shabanaj.beloyal.Dto.Registration.ActivationResponse;
import com.shabanaj.beloyal.Dto.Registration.RegisterUserDto;
import com.shabanaj.beloyal.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Enums.Role;
import com.shabanaj.beloyal.Enums.UserStatus;
import com.shabanaj.beloyal.Exception.RoleNotAllowedException;
import com.shabanaj.beloyal.Exception.TCNotAcceptedException;
import com.shabanaj.beloyal.Exception.TokenExpiredException;
import com.shabanaj.beloyal.Exception.TokenIsNotValidException;
import com.shabanaj.beloyal.Repository.CustomerProfileRepository;
import com.shabanaj.beloyal.Repository.UserRepository;
import com.shabanaj.beloyal.Security.CustomUserDetailsService;
import com.shabanaj.beloyal.Security.JwtService;
import com.shabanaj.beloyal.Service.AuthenticationService;
import com.shabanaj.beloyal.Service.EmailService;
import com.shabanaj.beloyal.Service.EmailVerificationTokenService;
import com.shabanaj.beloyal.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(UserService userService, EmailService emailService, UserRepository userRepository, CustomerProfileRepository customerProfileRepository, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtService jwtService, EmailVerificationTokenService emailVerificationTokenService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.emailVerificationTokenService = emailVerificationTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void registerCustomer(RegisterUserDto dto) {
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

        if(user.getRoles().isEmpty())
            user.getRoles().add(Role.CUSTOMER);
        else{
            throw new RoleNotAllowedException();
        }

        User savedUser= userService.createUser(user);

        //Create verification token and send it to user's email
        EmailVerificationToken verificationToken =emailVerificationTokenService.generateEmailVerificationToken(savedUser);

        emailService.sendActivationEmail(savedUser, verificationToken.getToken());
    }

    @Transactional
    public ActivationResponse activateUser(String token){
        EmailVerificationToken activationToken= emailVerificationTokenService.findEmailVerificationTokenByToken(token);

        if(activationToken.isUsed()){
            throw new TokenIsNotValidException();
        }

        if(activationToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Activation token has expired");
        }

        User currentUser= activationToken.getUser();
        currentUser.setStatus(UserStatus.ENABLED);
        currentUser.setEmailVerified(true);
        currentUser.setEmailVerifiedAt(LocalDateTime.now());

        emailVerificationTokenService.markTokenAsUsed(activationToken);

        // Generate jwt token
        UserDetails userDetails= customUserDetailsService.loadUserByUsername(currentUser.getEmail());
        String jwt= jwtService.generateToken(userDetails);

        currentUser.setLastLoginAt(LocalDateTime.now());
        userRepository.save(currentUser);

        return buildActivationResponse(currentUser, jwt, false);
    }

    private ActivationResponse buildActivationResponse(User user, String jwtToken, boolean alreadyVerified) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        boolean profileComplete = customerProfileRepository.findByUser(user).isPresent();

        return ActivationResponse.builder()
                .message(alreadyVerified ? "Already verified - logged in successfully" : "Email verified successfully!")
                .token(jwtToken)
                .tokenType("Bearer")
                .roles(roles)
                .emailVerified(true)
                .profileComplete(profileComplete)
                .alreadyVerified(alreadyVerified)
                .build();
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {
        // TODO: change the impelemntation of the login based on REQ-01
        Authentication auth= new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );
        authenticationManager.authenticate(auth);

        UserDetails userDetails= customUserDetailsService.loadUserByUsername(request.getEmail());
        String jwt= jwtService.generateToken(userDetails);

        User user= userRepository.findUserByEmail(request.getEmail())
                .orElseThrow();
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        LoginResponse response=new LoginResponse();
        response.setToken(jwt);
        response.setRoles(user.getRoles());
        return response;
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Email already verified");
        }

        EmailVerificationToken verificationToken =emailVerificationTokenService.generateEmailVerificationToken(user);

        emailService.sendActivationEmail(user, verificationToken.getToken());
    }
}
