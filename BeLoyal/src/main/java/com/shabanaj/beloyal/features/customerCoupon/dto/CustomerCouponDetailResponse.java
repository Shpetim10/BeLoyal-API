package com.shabanaj.beloyal.features.customerCoupon.dto;

import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class CustomerCouponDetailResponse {
    private Long id;
    private Long couponId;
    private Long businessId;
    private CustomerCouponStatus status;
    private int pointsSpent;
    private CurrencyCode currency;
    private LocalDateTime redeemedAt;
    private LocalDateTime usedAt;
    private LocalDateTime expiresAt;
    private String orderId;
    private String qrCode;
    private String snapshotTitle;
    private String snapshotDescription;
    private String snapshotImageUrl;
    private CouponType snapshotCouponType;
    private Long snapshotProductId;
    private Long snapshotVariantId;
    private BigDecimal snapshotDiscountPercentage;
    private BigDecimal snapshotDiscountAmount;
    private BigDecimal snapshotMinimumOrderAmount;
    private BigDecimal snapshotMaximumDiscountAmount;
    private LocalDateTime createdAt;
}
