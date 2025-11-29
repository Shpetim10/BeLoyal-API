package com.shabanaj.beloyal.Dto.Login;

import com.shabanaj.beloyal.Enums.Role;

import java.util.Set;

public class LoginResponse {
    private String token;
    private String tokenType= "Bearer";
    private Set<Role> roles;

    public LoginResponse() {
    }

    public LoginResponse(String token, String tokenType, Set<Role> roles) {
        this.token = token;
        this.tokenType = tokenType;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
