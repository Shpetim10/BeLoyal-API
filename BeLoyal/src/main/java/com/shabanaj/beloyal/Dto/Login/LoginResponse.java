package com.shabanaj.beloyal.Dto.Login;

import com.shabanaj.beloyal.Enums.Role;

import java.util.Set;

public class LoginResponse {
    private String token;
    private String tokenType= "Bearer";
    private Set<Role> roles;
    private boolean customerProfileComplete;
    private boolean emailVerified;

    public LoginResponse() {
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

    public boolean isCustomerProfileComplete() {
        return customerProfileComplete;
    }

    public void setCustomerProfileComplete(boolean customerProfileComplete) {
        this.customerProfileComplete = customerProfileComplete;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
