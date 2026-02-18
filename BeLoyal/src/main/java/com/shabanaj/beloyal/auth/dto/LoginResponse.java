package com.shabanaj.beloyal.auth.dto;

import com.shabanaj.beloyal.model.Enums.Role;

import java.util.List;
import java.util.Set;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresInSeconds;
    private Set<Role> roles;
    private boolean emailVerified;
    private boolean customerProfileComplete;
    private List<BusinessProfileInfo> businessProfiles;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getAccessTokenExpiresInSeconds() {
        return accessTokenExpiresInSeconds;
    }

    public void setAccessTokenExpiresInSeconds(long accessTokenExpiresInSeconds) {
        this.accessTokenExpiresInSeconds = accessTokenExpiresInSeconds;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isCustomerProfileComplete() {
        return customerProfileComplete;
    }

    public void setCustomerProfileComplete(boolean customerProfileComplete) {
        this.customerProfileComplete = customerProfileComplete;
    }

    public List<BusinessProfileInfo> getBusinessProfiles() {
        return businessProfiles;
    }

    public void setBusinessProfiles(List<BusinessProfileInfo> businessProfiles) {
        this.businessProfiles = businessProfiles;
    }
}
