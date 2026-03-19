package com.shabanaj.beloyal.features.registerLoyaltyPoints.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EarnComputationResult {
    private Integer totalPoints;
    private Integer equalShare;
    private Integer remainder;
    private Long primaryCustomerId;
    private List<GuestPointsResult> guestResults;
}
