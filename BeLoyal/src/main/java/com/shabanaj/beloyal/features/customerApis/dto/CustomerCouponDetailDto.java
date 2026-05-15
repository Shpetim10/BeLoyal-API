package com.shabanaj.beloyal.features.customerApis.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerCouponDetailDto(
    Long id,
    Long businessId,
    String businessName,
    String title,
    BigDecimal discountValue,
    String discountDisplay,
    String status,
    String type,
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
    Integer usageLimit,
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
    int customerRedemptionCount
) {
}
