package com.shabanaj.beloyal.features.loyaltyCard.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoyaltyCardDto {
    private String firstName;
    private String lastName;
    private String qrToken;
    private String manualCode;
}
