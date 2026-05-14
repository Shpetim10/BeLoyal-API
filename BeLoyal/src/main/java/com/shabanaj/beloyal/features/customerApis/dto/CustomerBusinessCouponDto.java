package com.shabanaj.beloyal.features.customerApis.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerBusinessCouponDto(
        // Owned-coupon row id (null for public/unowned coupons)
        Long customerCouponId,
        // The underlying coupon id (always present)
        Long couponId,
        Long businessId,
        String businessName,
        String title,
        String description,
        String imageUrl,
        String promotionType,
        String status,
        String discountDisplay,
        BigDecimal discountValue,
        int pointCost,
        String currency,
        boolean isUsed,
        boolean isOwned,
        int usageCount,
        Integer usageLimit,
        boolean isFeatured,
        int totalRedemptions,
        Integer totalRedemptionLimit,
        LocalDateTime startDate,
        LocalDateTime expiresAt,
        String termsAndConditions,

        // Discount rule details (from CouponDiscountDetails or snapshot)
        BigDecimal minimumOrderAmount,
        BigDecimal maximumDiscountAmount,

        // Free-product details (from CouponFreeProductDetails; null for non-FREE_PRODUCT coupons)
        Long freeProductCategoryId,
        String freeProductCategoryName,
        Long freeProductId,
        String freeProductName,
        Long freeVariantId,
        String freeVariantName,
        Integer freeQuantity,

        // Owned-coupon snapshot overrides (populated only when isOwned == true)
        String snapshotTitle,
        String snapshotDescription,
        String snapshotImageUrl,
        String snapshotCouponType,
        BigDecimal snapshotMinimumOrderAmount,
        BigDecimal snapshotMaximumDiscountAmount,

        // Owned-coupon lifecycle timestamps (null for public coupons)
        LocalDateTime redeemedAt,
        LocalDateTime usedAt,
        String orderId,

        // qr code
        String qrCode
) {
}
