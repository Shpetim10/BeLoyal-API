package com.shabanaj.beloyal.features.customerCoupon.dto;

import com.shabanaj.beloyal.model.Enums.CouponCannotRedeemCode;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class AvailableCouponItem {
    private Long couponId;
    private CouponType type;
    private String title;
    private String description;
    private String imageUrl;
    private int pointCost;
    private CurrencyCode currency;
    private String displayStatus;       // ACTIVE / EXPIRING
    private String discountDisplay;     // e.g. "10% Off", "5 EUR Off", "Free Product"
    private BigDecimal discountValue;   // numeric discount amount or percentage
    private LocalDateTime startDate;
    private LocalDateTime expiresAt;
    private Integer totalRedemptionLimit;
    private int totalRedemptions;
    private Integer perCustomerRedemptionLimit;
    private String termsAndConditions;
    private boolean isFeatured;
    private int customerRedemptionCount;
    private boolean canRedeem;
    private String cannotRedeemReason;
    private CouponCannotRedeemCode cannotRedeemCode;
}
