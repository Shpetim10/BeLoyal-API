package com.shabanaj.beloyal.registration.dto.businessRegistration;

public class VerifyOwnershipResponse {
    private boolean approved;
    private boolean emailVerified;
    private String ownershipToken;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getOwnershipToken() {
        return ownershipToken;
    }

    public void setOwnershipToken(String ownershipToken) {
        this.ownershipToken = ownershipToken;
    }
}
