package com.shabanaj.beloyal.features.customerCoupon.dto;

import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AvailableCouponItem {
    private Long couponId;
    private CouponType type;
    private String title;
    private String description;
    private String imageUrl;
    private int pointsCost;
    private CurrencyCode currency;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalRedemptionLimit;
    private int totalRedemptions;
    private Integer perCustomerRedemptionLimit;
    private String termsAndConditions;
    private boolean isFeatured;
    private boolean canRedeem;
    private String cannotRedeemReason;
}
