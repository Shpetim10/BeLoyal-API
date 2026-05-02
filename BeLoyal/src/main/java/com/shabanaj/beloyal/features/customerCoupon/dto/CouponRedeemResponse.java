package com.shabanaj.beloyal.features.customerCoupon.dto;

import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CouponRedeemResponse {
    private Long customerCouponId;
    private Long couponId;
    private CustomerCouponStatus status;
    private int pointsSpent;
    private int remainingBalance;
    private CurrencyCode currency;
    private LocalDateTime redeemedAt;
    private LocalDateTime expiresAt;
    private String snapshotTitle;
    private String snapshotDescription;
    private String snapshotImageUrl;
    private CouponType snapshotCouponType;
}
