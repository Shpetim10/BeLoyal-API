package com.shabanaj.beloyal.features.customerCoupon.dto;

import com.shabanaj.beloyal.model.Enums.CouponCannotRedeemCode;
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
    // identity
    private Long id;               // backward-compat alias for customerCouponId
    private Long customerCouponId; // explicit owned-row id
    private Long couponId;

    // business context
    private Long businessId;
    private String businessName;

    // display
    private String title;
    private String type;            // canonical CouponType name (FREE_PRODUCT / PERCENTAGE_DISCOUNT / FIXED_AMOUNT_DISCOUNT)
    private String displayStatus;   // ACTIVE / EXPIRING / USED / EXPIRED / CANCELLED
    private CustomerCouponStatus customerCouponStatus; // raw internal status

    // financial
    private int pointsSpent;
    private CurrencyCode currency;

    // lifecycle
    private LocalDateTime redeemedAt;
    private LocalDateTime usedAt;
    private LocalDateTime expiresAt;
    private String expiresIn;
    private boolean hasExpiry;
    private String orderId;
    private String qrCode;

    // top-level display fields (mirrors CustomerBusinessCouponDto for wallet consistency)
    private boolean isUsed;
    private String discountDisplay;
    private BigDecimal discountValue;
    // Free-product resolved names (snapshot first, live catalog fallback)
    private String freeProductCategory;
    private String freeProductName;
    private String freeProductVariant;
    private Integer freeProductQuantity;

    // canUse: this owned instance can be presented at checkout (REDEEMED and not expired)
    // canRedeem: customer can buy/claim another copy of the template right now
    private boolean canUse;
    private String cannotUseReason;

    // snapshot
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

    // template-sourced display fields (enriched at call time)
    private boolean isFeatured;
    private Integer perCustomerRedemptionLimit;
    private int totalRedemptions;
    private Integer totalRedemptionLimit;
    private int customerRedemptionCount;
    private boolean canRedeem;
    private String cannotRedeemReason;
    private CouponCannotRedeemCode cannotRedeemCode;
}
