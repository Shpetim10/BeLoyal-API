package com.shabanaj.beloyal.features.loyaltySettings.dto;

import com.shabanaj.beloyal.model.Enums.ExpiryType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLoyaltySettingsDto {
    @NotNull
    @Min(1)
    private Integer minPointsToRedeem;

    @NotNull
    @Min(1)
    private Integer maxPointsToRedeem;

    @NotNull
    @Min(0)
    private Integer pointsPerUnitDiscount;

    @NotNull
    @Min(0)
    private Integer maxPointsPerTransaction;

    @NotNull
    private ExpiryType expiryType;

    @Min(0)
    private Integer monthsToExpire;
}
