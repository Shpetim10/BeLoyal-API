package com.shabanaj.beloyal.features.auth.controller;

import com.shabanaj.beloyal.features.auth.dto.LogoutRequest;
import com.shabanaj.beloyal.features.auth.dto.RefreshRequest;
import com.shabanaj.beloyal.features.auth.dto.LoginRequest;
import com.shabanaj.beloyal.features.auth.dto.LoginResponse;
import com.shabanaj.beloyal.features.auth.service.LoginService;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.VerifyOwnershipRequest;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.VerifyOwnershipResponse;
import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.ActivationResponse;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.common.Exception.InvalidCredentialsException;
import com.shabanaj.beloyal.common.Exception.TokenExpiredException;
import com.shabanaj.beloyal.common.Exception.TokenIsNotValidException;
import com.shabanaj.beloyal.features.registration.service.CustomerRegistrationService;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.features.auth.service.AuthenticationService;
import com.shabanaj.beloyal.features.registration.service.BusinessRegistrationService;
import com.shabanaj.beloyal.features.token.service.RefreshTokenService;
import com.shabanaj.beloyal.features.user.service.UserActivationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/besahub/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final BusinessRegistrationService businessRegistrationService;
    private final LoginService loginService;
    private final UserActivationService userActivationService;
    private final CustomerRegistrationService customerRegistrationService;

    private Logger logger= LogManager.getLogger(AuthController.class);

    // Verify user by the app
    @GetMapping("/verify-email")
    public ResponseEntity<?> activateAccountFromApp(@RequestParam String token) {
        try {
            // Activate user and get authentication details
            ActivationResponse response = userActivationService.activateUser(token);

            return ResponseEntity.ok(response);

        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(Map.of(
                            "error", "TOKEN_EXPIRED",
                            "message", "Activation link has expired. Please request a new one."
                    ));

        } catch (TokenIsNotValidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "INVALID_TOKEN",
                            "message", "Invalid activation link."
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "ACTIVATION_FAILED",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateAccount(@RequestParam String token) {
        try {
            // Activate user and get authentication details
            ActivationResponse response = userActivationService.activateUser(token);

            return ResponseEntity.ok(response);

        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(Map.of(
                            "error", "TOKEN_EXPIRED",
                            "message", "Activation link has expired. Please request a new one."
                    ));

        } catch (TokenIsNotValidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "INVALID_TOKEN",
                            "message", "Invalid activation link."
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "ACTIVATION_FAILED",
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam String email) {
        try {
            authenticationService.resendVerificationEmail(email);
            return ResponseEntity.ok(Map.of(
                    "message", "Verification email sent successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest){
        try{
            return ResponseEntity.ok(loginService.login(loginRequest));
        }catch(Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody @Valid RefreshRequest req) {
        return ResponseEntity.ok(authenticationService.refresh(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequest req) {
        logger.info("Logout user inside auth controller");
        authenticationService.logOut(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal UserDetails user) {
        User u = userRepository.findUserByEmailIgnoreCase(user.getUsername()).orElseThrow();
        refreshTokenService.revokeAllForUser(u.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-ownership")
    public ResponseEntity<VerifyOwnershipResponse> verifyOwnership(@RequestBody @Valid VerifyOwnershipRequest req) {
        try{
            VerifyOwnershipResponse response = authenticationService.verifyOwnership(req);

            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {
            VerifyOwnershipResponse response = new  VerifyOwnershipResponse();
            response.setApproved(false);
            response.setEmailVerified(false);
            response.setOwnershipToken(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
