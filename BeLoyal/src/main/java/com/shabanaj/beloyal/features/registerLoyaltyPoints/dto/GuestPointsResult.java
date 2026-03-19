package com.shabanaj.beloyal.features.registerLoyaltyPoints.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestPointsResult {
    private Long customerId;
    private Integer earnedPoints;
    private Integer currentBalance;
}
