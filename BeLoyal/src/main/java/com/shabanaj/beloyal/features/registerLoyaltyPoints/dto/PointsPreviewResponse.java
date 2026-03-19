package com.shabanaj.beloyal.features.registerLoyaltyPoints.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PointsPreviewResponse {
    private Integer totalPoints;
    private Integer remainingPoints;
    private Long primaryCustomerId;
    private Integer pointsPer;
    private BigDecimal amountPer;
    private Integer maxPointsPerTransaction;
    private List<GuestPointsResult> guestPointsResults;
}
