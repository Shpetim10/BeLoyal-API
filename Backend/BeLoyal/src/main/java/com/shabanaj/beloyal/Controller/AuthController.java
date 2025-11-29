package com.shabanaj.beloyal.Controller;

import com.shabanaj.beloyal.Dto.Login.LoginRequest;
import com.shabanaj.beloyal.Dto.Login.LoginResponse;
import com.shabanaj.beloyal.Dto.RegisterUserDto;
import com.shabanaj.beloyal.Service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/beloyal/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String token) {
        try{
            authenticationService.activateUser(token);

            return  ResponseEntity.ok("User was successfully activated!");
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterUserDto dto) {
        try{
            authenticationService.registerUser(dto);

            return  ResponseEntity.ok("User successfully registered!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        try{
            return ResponseEntity.ok(authenticationService.loginUser(loginRequest));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }
}
