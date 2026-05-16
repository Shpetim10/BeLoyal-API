package com.shabanaj.beloyal.features.customerApis.dto;

import com.shabanaj.beloyal.model.Enums.CouponCannotRedeemCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerPromotionDto(
        Long id,               // customerCouponId for owned coupons; null for public/unowned
        Long businessId,
        Long couponId,         // underlying loyalty coupon template id (always present)
        String businessName,
        String title,
        String description,
        String promotionType,  // legacy field — kept for backward compatibility
        String status,         // display status: ACTIVE / EXPIRING / USED / EXPIRED
        String discountDisplay,
        int pointCost,
        LocalDateTime expiresAt,
        String expiresIn,
        List<String> gradientHex,
        boolean isHot,
        boolean isUsed,
        int usageCount,
        Integer perCustomerRedemptionLimit,
        String termsAndConditions,
        boolean isOwned,
        String qrCode,
        int customerRedemptionCount,
        boolean canRedeem,
        String cannotRedeemReason,
        boolean isFeatured,
        int totalRedemptions,
        Integer totalRedemptionLimit,
        CouponCannotRedeemCode cannotRedeemCode,
        // Fields matching CustomerBusinessCouponDto
        String imageUrl,
        String type,
        BigDecimal discountValue,
        String currency,
        LocalDateTime startDate,
        BigDecimal minimumOrderAmount,
        BigDecimal maximumDiscountAmount,
        Long freeProductCategoryId,
        String freeProductCategory,
        Long freeProductId,
        String freeProductName,
        Long freeVariantId,
        String freeProductVariant,
        Integer freeProductQuantity,
        String snapshotTitle,
        String snapshotDescription,
        String snapshotImageUrl,
        String snapshotCouponType,
        BigDecimal snapshotMinimumOrderAmount,
        BigDecimal snapshotMaximumDiscountAmount,
        LocalDateTime redeemedAt,
        LocalDateTime usedAt,
        String orderId
) {
}
