package com.shabanaj.beloyal.features.customerCoupon.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateRedemptionResponse {
    private boolean canRedeem;
    private String reason;
    private int pointsRequired;
    private int customerBalance;
}
