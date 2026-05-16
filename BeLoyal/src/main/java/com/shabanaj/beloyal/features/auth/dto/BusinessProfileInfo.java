package com.shabanaj.beloyal.features.auth.dto;

import com.shabanaj.beloyal.model.Enums.CurrencyCode;
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
    private String statusDisplayName;
    private String statusDescription;
    private String rejectionReason;
    private String memberStatus;
    private boolean invitationAccepted;

    // Settings configuration fields
    private boolean earningSettingsEnabled;
    private boolean earningSettingsConfigured;
    private boolean loyaltySettingsEnabled;
    private boolean loyaltySettingsConfigured;
    private CurrencyCode currency;
}
