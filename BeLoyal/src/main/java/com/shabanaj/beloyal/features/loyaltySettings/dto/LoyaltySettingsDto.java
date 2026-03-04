package com.shabanaj.beloyal.features.loyaltySettings.dto;

import com.shabanaj.beloyal.model.Enums.ExpiryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoyaltySettingsDto {
    private Integer minPointsToRedeem;
    private Integer maxPointsToRedeem;
    private Integer pointsPerUnitDiscount;
    private Integer maxPointsPerTransactions;
    private ExpiryType expiryType;
    private Integer monthsToExpire;
}
