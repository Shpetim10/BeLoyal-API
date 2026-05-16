package com.shabanaj.beloyal.features.customerApis.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerCouponDetailDto(
    Long couponId,
    Long businessId,
    String businessName,
    String title,
    BigDecimal discountValue,
    String discountDisplay,
    String status,           // raw CouponStatus / CustomerCouponStatus label
    String displayStatus,    // normalized: ACTIVE / EXPIRING / USED / EXPIRED
    String type,             // canonical: FREE_PRODUCT / PERCENTAGE_DISCOUNT / FIXED_AMOUNT_DISCOUNT
    LocalDateTime expiresAt,
    LocalDateTime startDate,
    Integer pointCost,
    String description,
    String termsAndConditions,
    String imageUrl,
    String currency,
    Boolean isFeatured,
    Boolean isUsed,
    Boolean isOwned,
    Boolean isHot,
    Integer totalRedemptions,
    Integer totalRedemptionLimit,
    Integer perCustomerRedemptionLimit,
    Integer usageCount,
    Long customerCouponId,
    BigDecimal minimumOrderAmount,
    BigDecimal maximumDiscountAmount,
    String freeProductCategory,
    String freeProductName,
    String freeProductVariant,
    Integer freeProductQuantity,
    LocalDateTime redeemedAt,
    LocalDateTime usedAt,
    String orderId,
    String qrCode,
    String multiplierLabel,
    String expiresIn,
    int customerRedemptionCount,
    boolean canRedeem,
    String cannotRedeemReason
) {
}
