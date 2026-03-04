package com.shabanaj.beloyal.features.auth.dto;

import com.shabanaj.beloyal.model.Enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessProfileInfo {
    private Long businessId;
    private String businessName;
    private Role role;
    private boolean active;
    private String businessStatus;
    private String rejectionReason;
    private String memberStatus;

    // Settings configuration fields
    private boolean earningSettingsEnabled;
    private boolean earningSettingsConfigured;
    private boolean loyaltySettingsEnabled;
    private boolean loyaltySettingsConfigured;
}
