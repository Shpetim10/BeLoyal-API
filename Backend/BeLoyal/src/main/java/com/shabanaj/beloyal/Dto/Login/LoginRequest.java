package com.shabanaj.beloyal.Dto.Login;

import com.shabanaj.beloyal.Validation.Annotation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginRequest {
    @ValidEmail
    private String email;
    @NotBlank
    @NotNull
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
