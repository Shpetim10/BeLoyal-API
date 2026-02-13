package com.shabanaj.beloyal.Controller;

import com.shabanaj.beloyal.Dto.Login.LoginRequest;
import com.shabanaj.beloyal.Dto.Login.LoginResponse;
import com.shabanaj.beloyal.Dto.Registration.RegisterUserDto;
import com.shabanaj.beloyal.Service.AuthenticationService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    public ResponseEntity<?> activateAccount(@RequestParam String token) {
        try{
            authenticationService.activateUser(token);

            return  ResponseEntity.ok(Map.of("message","User was successfully activated!"));
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateAccountViaWeb(@RequestParam String token) {
        try{
            authenticationService.activateUser(token);

            return  ResponseEntity.ok(Map.of("message","User was successfully activated!"));
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
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
