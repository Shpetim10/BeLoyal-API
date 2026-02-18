package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Configurations.SystemSettings;
import com.shabanaj.beloyal.Dto.Auth.LogoutRequest;
import com.shabanaj.beloyal.Dto.Auth.RefreshRequest;
import com.shabanaj.beloyal.Dto.Login.LoginRequest;
import com.shabanaj.beloyal.Dto.Login.LoginResponse;
import com.shabanaj.beloyal.Dto.Registration.businessRegistration.VerifyOwnershipRequest;
import com.shabanaj.beloyal.Dto.Registration.businessRegistration.VerifyOwnershipResponse;
import com.shabanaj.beloyal.Dto.Registration.customerRegistraton.ActivationResponse;
import com.shabanaj.beloyal.Dto.Registration.customerRegistraton.RegisterUserDto;
import com.shabanaj.beloyal.Entity.BusinessMember;
import com.shabanaj.beloyal.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.Entity.RefreshToken;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Enums.BusinessStatus;
import com.shabanaj.beloyal.Enums.Role;
import com.shabanaj.beloyal.Enums.UserStatus;
import com.shabanaj.beloyal.Exception.*;
import com.shabanaj.beloyal.Repository.BusinessMemberRepository;
import com.shabanaj.beloyal.Repository.BusinessRepository;
import com.shabanaj.beloyal.Repository.CustomerProfileRepository;
import com.shabanaj.beloyal.Repository.UserRepository;
import com.shabanaj.beloyal.Security.CustomUserDetailsService;
import com.shabanaj.beloyal.Security.JwtService;
import com.shabanaj.beloyal.Security.OwnershipTokenService;
import com.shabanaj.beloyal.Service.*;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
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
    private final BusinessRepository businessRepository;
    private final BusinessMemberRepository businessMemberRepository;
    private final Logger logger= LogManager.getLogger(AuthenticationServiceImpl.class);
    private final RefreshTokenService refreshTokenService;
    private final OwnershipTokenService ownershipTokenService;

    public AuthenticationServiceImpl(UserService userService, EmailService emailService, UserRepository userRepository, CustomerProfileRepository customerProfileRepository, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtService jwtService, EmailVerificationTokenService emailVerificationTokenService, PasswordEncoder passwordEncoder, BusinessRepository businessRepository, BusinessMemberRepository businessMemberRepository, RefreshTokenService refreshTokenService, OwnershipTokenService ownershipTokenService) {
        this.userService = userService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.emailVerificationTokenService = emailVerificationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.businessRepository = businessRepository;
        this.businessMemberRepository = businessMemberRepository;
        this.refreshTokenService = refreshTokenService;
        this.ownershipTokenService = ownershipTokenService;
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

        UserDetails userDetails= customUserDetailsService.loadUserByUsername(currentUser.getEmail());
        // Generate access token
        String jwt = jwtService.generateAccessToken(userDetails);
        // create refresh token
        String refresh = refreshTokenService.create(currentUser, null, null);

        currentUser.setLastLoginAt(LocalDateTime.now());
        userRepository.save(currentUser);

        return buildActivationResponse(currentUser, jwt, refresh,false);
    }

    private ActivationResponse buildActivationResponse(User user, String jwtToken, String refreshToken, boolean alreadyVerified) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        boolean profileComplete = customerProfileRepository.findByUser(user).isPresent();

        return ActivationResponse.builder()
                .message(alreadyVerified ? "Already verified - logged in successfully" : "Email verified successfully!")
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .roles(roles)
                .emailVerified(true)
                .profileComplete(profileComplete)
                .alreadyVerified(alreadyVerified)
                .build();
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(InvalidCredentialsException::new);

        LocalDateTime now = LocalDateTime.now();

        // Lock handling
        if (user.getStatus() == UserStatus.LOCKED) {
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(now)) {
                throw new UserIsLockedException();
            }
            logger.info("Lock time has passed");

            // lock expired -> unlock
            user.setStatus(UserStatus.ENABLED);
            user.setLockedUntil(null);
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }

        // Disabled handling
        if (user.getStatus() != UserStatus.ENABLED && user.getStatus()!= UserStatus.PENDING_VERIFICATION) {
            throw new UserIsDisabledException();
        }

        // Handle inactive business and staff, if he is not customer
        if ((user.getRoles().contains(Role.STAFF) || user.getRoles().contains(Role.BUSINESS_ADMIN)) && !user.getRoles().contains(Role.CUSTOMER)) {
            BusinessMember businessMember= businessMemberRepository.findByUser(user)
                    .orElseThrow(()-> new AccessDeniedException("Business Member Not Found"));

            logger.info("Business Member has been found");

            if (!businessMember.getMemberStatus().equals(UserStatus.ENABLED) || !businessMember.getBusiness().getBusinessStatus().equals(BusinessStatus.ACTIVE)) {
                throw new InactiveBusinessException("Staff or business is not active");
            }
            logger.info("Business and Member has been activated");
        }

        try {
            Authentication auth = new UsernamePasswordAuthenticationToken(email, request.getPassword());
            authenticationManager.authenticate(auth);
        } catch (AuthenticationException ex) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= SystemSettings.MAX_LOGIN_ATTEMPTS) {
                user.setStatus(UserStatus.LOCKED);
                user.setLockedUntil(now.plusMinutes(SystemSettings.LOCK_MINUTES));
            }

            userRepository.save(user);
            throw new InvalidCredentialsException();
        }

        // Success bookkeeping
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(now);
        userRepository.save(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        String access = jwtService.generateAccessToken(userDetails);

        // create refresh token
        String refresh = refreshTokenService.create(user, null, null);

        LoginResponse res = new LoginResponse();
        res.setAccessToken(access);
        res.setRefreshToken(refresh);
        res.setAccessTokenExpiresInSeconds(15 * 60);
        res.setRoles(user.getRoles());
        res.setEmailVerified(user.getEmailVerifiedAt() != null);

        res.setCustomerProfileComplete(
                user.getRoles().contains(Role.CUSTOMER) &&
                        customerProfileRepository.findByUser(user).isPresent()
        );

        return res;
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

    @Override
    public LoginResponse refresh(RefreshRequest refreshRequest) {
        RefreshToken existing = refreshTokenService.validate(refreshRequest.refreshToken());

        User user = existing.getUser();
        if (user.getStatus() != UserStatus.ENABLED) throw new AccessDeniedException("Access denied");

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String newAccess = jwtService.generateAccessToken(userDetails);
        String newRefresh = refreshTokenService.rotate(existing);

        LoginResponse res = new LoginResponse();
        res.setAccessToken(newAccess);
        res.setRefreshToken(newRefresh);
        res.setAccessTokenExpiresInSeconds(15 * 60);
        res.setRoles(user.getRoles());
        res.setEmailVerified(user.getEmailVerifiedAt() != null);
        res.setCustomerProfileComplete(
                user.getRoles().contains(Role.CUSTOMER) &&
                        customerProfileRepository.findByUser(user).isPresent()
        );
        return res;
    }

    @Override
    public void logOut(LogoutRequest request){
        logger.info("Logout inside auth service");
        refreshTokenService.revoke(request.refreshToken());
    }

    @Override
    public VerifyOwnershipResponse verifyOwnership(VerifyOwnershipRequest verifyOwnershipRequest) {
        String email = verifyOwnershipRequest.getEmail().trim().toLowerCase();

        Optional<User> user= userRepository.findUserByEmailIgnoreCase(email);

        if(!user.isPresent()){
            throw new InvalidCredentialsException();
        }

        if(!passwordEncoder.matches(verifyOwnershipRequest.getPassword(),user.get().getPasswordHash())){
            throw new InvalidCredentialsException();
        }

        String ownershipToken= ownershipTokenService.issue(user.get().getId(), user.get().getEmail());

        VerifyOwnershipResponse res = new VerifyOwnershipResponse();
        res.setApproved(true);
        res.setEmailVerified(user.get().getEmailVerifiedAt() != null);
        res.setOwnershipToken(ownershipToken);
        return res;
    }
}
