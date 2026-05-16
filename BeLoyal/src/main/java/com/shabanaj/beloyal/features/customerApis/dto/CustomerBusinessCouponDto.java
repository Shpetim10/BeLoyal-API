package com.shabanaj.beloyal.features.customerApis.dto;

import com.shabanaj.beloyal.model.Enums.CouponCannotRedeemCode;

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
        String promotionType,  // legacy — kept for backward compatibility
        String type,           // canonical: FREE_PRODUCT / PERCENTAGE_DISCOUNT / FIXED_AMOUNT_DISCOUNT
        String status,
        String discountDisplay,
        BigDecimal discountValue,
        int pointCost,
        String currency,
        boolean isUsed,
        boolean isOwned,
        int usageCount,
        Integer perCustomerRedemptionLimit,
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
        String freeProductCategory,
        Long freeProductId,
        String freeProductName,
        Long freeVariantId,
        String freeProductVariant,
        Integer freeProductQuantity,

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
        String qrCode,

        // human-readable expiry label
        String expiresIn,

        // how many times this customer has bought this coupon
        int customerRedemptionCount,

        // claimability gate (can this customer buy/claim this coupon right now)
        boolean canRedeem,
        String cannotRedeemReason,
        CouponCannotRedeemCode cannotRedeemCode
) {
}
