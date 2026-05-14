package com.shabanaj.beloyal.features.registerLoyaltyPoints.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class EarnPointsTransactionResponse {
    private String status; // CREATED or REPLAYED
    private BigDecimal billAmount;
    private BigDecimal originalBillAmount;
    private BigDecimal couponDiscountApplied;
    private Long appliedCustomerCouponId;
    private String transactionReference;
    private String note;

    // transaction description
    private Integer totalPoints;
    private Integer remainingPoints;
    private Long primaryCustomerId;
    private Integer pointsPer;
    private BigDecimal amountPer;
    private Integer maxPointsPerTransaction;
    private List<GuestPointsResult> guestPointsResults;
}
