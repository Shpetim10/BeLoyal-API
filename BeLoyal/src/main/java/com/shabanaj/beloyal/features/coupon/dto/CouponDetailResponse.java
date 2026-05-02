package com.shabanaj.beloyal.features.coupon.dto;

import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CouponVisibility;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CouponDetailResponse {
    private Long id;
    private Long businessId;
    private CouponType type;
    private String title;
    private String description;
    private String imageUrl;
    private int pointsCost;
    private CurrencyCode currency;
    private CouponStatus status;
    private CouponVisibility visibility;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalRedemptionLimit;
    private int totalRedemptions;
    private Integer perCustomerRedemptionLimit;
    private String termsAndConditions;
    private boolean isFeatured;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CouponFreeProductDetailsDto freeProductDetails;
    private CouponDiscountDetailsDto discountDetails;
}
