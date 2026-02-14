package com.shabanaj.beloyal.Controller;

import com.shabanaj.beloyal.Dto.Login.LoginRequest;
import com.shabanaj.beloyal.Dto.Login.LoginResponse;
import com.shabanaj.beloyal.Dto.Registration.ActivationResponse;
import com.shabanaj.beloyal.Dto.Registration.RegisterUserDto;
import com.shabanaj.beloyal.Exception.TokenExpiredException;
import com.shabanaj.beloyal.Exception.TokenIsNotValidException;
import com.shabanaj.beloyal.Service.AuthenticationService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/beloyal/auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private Logger logger= LogManager.getLogger(AuthController.class);

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // Verify user by the app
    @GetMapping("/verify-email")
    public ResponseEntity<?> activateAccountFromApp(@RequestParam String token) {
        try {
            // Activate user and get authentication details
            ActivationResponse response = authenticationService.activateUser(token);

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
            ActivationResponse response = authenticationService.activateUser(token);

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

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterUserDto dto) {
        try{
            authenticationService.registerCustomer(dto);

            return  ResponseEntity.ok("User successfully registered!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest){
        try{
            return ResponseEntity.ok(authenticationService.loginUser(loginRequest));
        }catch(Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
