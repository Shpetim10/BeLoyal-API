package com.shabanaj.beloyal.features.userProfiles.customer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerProfileCreationResponse {
    private String firstName;
    private String lastName;
    private String qrToken;
    private String manualCode;
}
