package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.common.Exception.CouponNotFound;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerCouponDetailDto;
import com.shabanaj.beloyal.features.customerApis.service.CustomerCouponDetailService;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CouponVisibility;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerCouponDetailServiceImpl implements CustomerCouponDetailService {

    private final UserService userService;
    private final CustomerProfileService customerProfileService;
    private final CouponRepository couponRepository;
    private final CustomerCouponRepository customerCouponRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomerCouponDetailDto getCouponDetail(Long userId, Long couponId) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        LoyaltyCoupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFound(couponId));

        LocalDateTime now = LocalDateTime.now();
        boolean isPublicActive = coupon.getStatus() == CouponStatus.ACTIVE
                && coupon.getVisibility() == CouponVisibility.PUBLIC
                && coupon.getStartDate().isBefore(now)
                && coupon.getEndDate().isAfter(now)
                && coupon.getDeletedAt() == null;

        // Use findTopBy... to avoid IncorrectResultSizeDataAccessException when customer
        // has redeemed this coupon more than once. Most recent REDEEMED row wins.
        List<CustomerCoupon> ownedRows = customerCouponRepository
                .findAllByCouponIdAndCustomerProfileIdOrderByCreatedAtDesc(couponId, customerProfile.getId());

        CustomerCoupon ownedCoupon = ownedRows.stream()
                .filter(cc -> cc.getStatus() == CustomerCouponStatus.REDEEMED)
                .filter(cc -> cc.getExpiresAt() == null || cc.getExpiresAt().isAfter(now))
                .findFirst()
                .orElseGet(() -> ownedRows.isEmpty() ? null : ownedRows.get(0));
        boolean isOwnedByCustomer = ownedCoupon != null;

        if (!isPublicActive && !isOwnedByCustomer) {
            throw new CouponNotFound(couponId);
        }

        int customerRedemptionCount = ownedRows.size();

        // canRedeem: can the customer claim another copy of this template right now?
        int balance = loyaltyAccountRepository
                .findByCustomerProfileAndBusiness(customerProfile, coupon.getBusiness())
                .map(LoyaltyAccount::getAvailablePoints)
                .orElse(0);

        boolean canRedeem = true;
        String cannotRedeemReason = null;

        if (coupon.getStatus() != CouponStatus.ACTIVE || !isPublicActive) {
            canRedeem = false;
            cannotRedeemReason = "Coupon no longer available";
        } else if (balance < coupon.getPointsCost()) {
            canRedeem = false;
            cannotRedeemReason = "Insufficient points";
        } else if (coupon.isSoldOut()) {
            canRedeem = false;
            cannotRedeemReason = "Coupon is sold out";
        } else if (coupon.getPerCustomerRedemptionLimit() != null
                && customerRedemptionCount >= coupon.getPerCustomerRedemptionLimit()) {
            canRedeem = false;
            cannotRedeemReason = "Redemption limit reached";
        }

        return buildDetail(coupon, ownedCoupon, now, customerRedemptionCount, canRedeem, cannotRedeemReason);
    }

    private CustomerCouponDetailDto buildDetail(LoyaltyCoupon coupon, CustomerCoupon ownedCoupon, LocalDateTime now,
                                                 int customerRedemptionCount, boolean canRedeem, String cannotRedeemReason) {
        String rawStatus = deriveRawStatus(coupon, ownedCoupon, now);
        String displayStatus = deriveDisplayStatus(coupon, ownedCoupon, now);
        String discountDisplay = buildDiscountDisplay(coupon, ownedCoupon);
        BigDecimal discountValue = extractDiscountValue(coupon, ownedCoupon);

        Long customerCouponId = ownedCoupon != null ? ownedCoupon.getId() : null;
        Boolean isUsed = ownedCoupon != null && ownedCoupon.getStatus() == CustomerCouponStatus.USED;
        Integer usageCount = Boolean.TRUE.equals(isUsed) ? 1 : 0;
        LocalDateTime redeemedAt = ownedCoupon != null ? ownedCoupon.getRedeemedAt() : null;
        LocalDateTime usedAt = ownedCoupon != null ? ownedCoupon.getUsedAt() : null;
        String orderId = ownedCoupon != null ? ownedCoupon.getOrderId() : null;
        String qrCode = ownedCoupon != null ? ownedCoupon.getQrCode() : null;

        String title = (ownedCoupon != null && ownedCoupon.getSnapshotTitle() != null)
                ? ownedCoupon.getSnapshotTitle() : coupon.getTitle();
        String description = (ownedCoupon != null && ownedCoupon.getSnapshotDescription() != null)
                ? ownedCoupon.getSnapshotDescription() : coupon.getDescription();
        String imageUrl = (ownedCoupon != null && ownedCoupon.getSnapshotImageUrl() != null)
                ? ownedCoupon.getSnapshotImageUrl() : coupon.getImageUrl();
        int pointCost = ownedCoupon != null ? ownedCoupon.getPointsSpent() : coupon.getPointsCost();

        BigDecimal minimumOrderAmount = null;
        BigDecimal maximumDiscountAmount = null;
        String freeProductCategory = null;
        String freeProductName = null;
        String freeProductVariant = null;
        Integer freeProductQuantity = null;

        if (coupon.getType() == CouponType.PERCENTAGE_DISCOUNT || coupon.getType() == CouponType.FIXED_AMOUNT_DISCOUNT) {
            if (ownedCoupon != null) {
                minimumOrderAmount = ownedCoupon.getSnapshotMinimumOrderAmount();
                maximumDiscountAmount = ownedCoupon.getSnapshotMaximumDiscountAmount();
            }
            CouponDiscountDetails discountDetails = coupon.getDiscountDetails();
            if (discountDetails != null) {
                if (minimumOrderAmount == null) minimumOrderAmount = discountDetails.getMinimumOrderAmount();
                if (maximumDiscountAmount == null) maximumDiscountAmount = discountDetails.getMaximumDiscountAmount();
            }
        }

        if (coupon.getType() == CouponType.FREE_PRODUCT) {
            CouponFreeProductDetails freeProductDetails = coupon.getFreeProductDetails();
            if (freeProductDetails != null) {
                freeProductCategory = freeProductDetails.getCategory().getName();
                freeProductName = freeProductDetails.getProduct().getName();
                if (freeProductDetails.getVariant() != null) {
                    freeProductVariant = freeProductDetails.getVariant().getName();
                }
                freeProductQuantity = freeProductDetails.getQuantity();
            }
        }

        LocalDateTime expiresAt = ownedCoupon != null ? ownedCoupon.getExpiresAt() : coupon.getEndDate();
        return new CustomerCouponDetailDto(
            coupon.getId(),
            coupon.getBusiness().getId(),
            coupon.getBusiness().getBusinessName(),
            title,
            discountValue,
            discountDisplay,
            rawStatus,
            displayStatus,
            coupon.getType().name(),
            expiresAt,
            coupon.getStartDate(),
            pointCost,
            description,
            coupon.getTermsAndConditions(),
            imageUrl,
            coupon.getCurrency().name(),
            coupon.isFeatured(),
            isUsed,
            ownedCoupon != null,
            false,
            coupon.getTotalRedemptions(),
            coupon.getTotalRedemptionLimit(),
            coupon.getPerCustomerRedemptionLimit(),
            usageCount,
            customerCouponId,
            minimumOrderAmount,
            maximumDiscountAmount,
            freeProductCategory,
            freeProductName,
            freeProductVariant,
            freeProductQuantity,
            redeemedAt,
            usedAt,
            orderId,
            qrCode,
            null,
            buildExpiresIn(expiresAt, now),
            customerRedemptionCount,
            canRedeem,
            cannotRedeemReason
        );
    }

    private String deriveRawStatus(LoyaltyCoupon coupon, CustomerCoupon ownedCoupon, LocalDateTime now) {
        if (ownedCoupon != null) {
            return ownedCoupon.getStatus().name();
        }
        if (coupon.getStatus() != CouponStatus.ACTIVE) return coupon.getStatus().name();
        if (coupon.getEndDate().isBefore(now)) return "EXPIRED";
        return "ACTIVE";
    }

    private String deriveDisplayStatus(LoyaltyCoupon coupon, CustomerCoupon ownedCoupon, LocalDateTime now) {
        if (ownedCoupon != null) {
            if (ownedCoupon.getStatus() == CustomerCouponStatus.USED) return "USED";
            if (ownedCoupon.getStatus() == CustomerCouponStatus.EXPIRED) return "EXPIRED";
            if (ownedCoupon.getStatus() == CustomerCouponStatus.CANCELLED) return "CANCELLED";
            // REDEEMED → EXPIRED if past snapshot expiry, EXPIRING if within 3 days, else ACTIVE
            if (ownedCoupon.getExpiresAt() != null && ownedCoupon.getExpiresAt().isBefore(now)) {
                return "EXPIRED";
            }
            if (ownedCoupon.getExpiresAt() != null && ownedCoupon.getExpiresAt().isBefore(now.plusDays(3))) {
                return "EXPIRING";
            }
            return "ACTIVE";
        }

        if (coupon.getStatus() != CouponStatus.ACTIVE) return "EXPIRED";
        if (coupon.getEndDate().isBefore(now)) return "EXPIRED";
        if (coupon.getEndDate().isBefore(now.plusDays(3))) return "EXPIRING";
        return "ACTIVE";
    }

    private String buildDiscountDisplay(LoyaltyCoupon coupon, CustomerCoupon ownedCoupon) {
        if (coupon.getType() == CouponType.FREE_PRODUCT) {
            return "Free Product";
        }

        if (coupon.getType() == CouponType.PERCENTAGE_DISCOUNT) {
            if (ownedCoupon != null && ownedCoupon.getSnapshotDiscountPercentage() != null) {
                return ownedCoupon.getSnapshotDiscountPercentage().stripTrailingZeros().toPlainString() + "% Off";
            }
            CouponDiscountDetails discountDetails = coupon.getDiscountDetails();
            if (discountDetails != null && discountDetails.getDiscountPercentage() != null) {
                return discountDetails.getDiscountPercentage().stripTrailingZeros().toPlainString() + "% Off";
            }
        }

        if (coupon.getType() == CouponType.FIXED_AMOUNT_DISCOUNT) {
            if (ownedCoupon != null && ownedCoupon.getSnapshotDiscountAmount() != null) {
                return ownedCoupon.getSnapshotDiscountAmount().stripTrailingZeros().toPlainString()
                        + " " + coupon.getCurrency().getSymbol() + " Off";
            }
            CouponDiscountDetails discountDetails = coupon.getDiscountDetails();
            if (discountDetails != null && discountDetails.getDiscountAmount() != null) {
                return discountDetails.getDiscountAmount().stripTrailingZeros().toPlainString()
                        + " " + coupon.getCurrency().getSymbol() + " Off";
            }
        }

        return null;
    }

    private String buildExpiresIn(LocalDateTime expiresAt, LocalDateTime now) {
        if (expiresAt == null) return null;
        Duration diff = Duration.between(now, expiresAt);
        if (diff.isNegative()) {
            long daysAgo = Math.abs(diff.toDays());
            return "Expired " + daysAgo + "d ago";
        }
        long totalHours = diff.toHours();
        if (totalHours < 24) {
            return "Expires in " + totalHours + "h";
        }
        return "Expires in " + diff.toDays() + "d";
    }

    private BigDecimal extractDiscountValue(LoyaltyCoupon coupon, CustomerCoupon ownedCoupon) {
        if (coupon.getType() == CouponType.PERCENTAGE_DISCOUNT) {
            if (ownedCoupon != null && ownedCoupon.getSnapshotDiscountPercentage() != null) {
                return ownedCoupon.getSnapshotDiscountPercentage();
            }
            CouponDiscountDetails discountDetails = coupon.getDiscountDetails();
            if (discountDetails != null) {
                return discountDetails.getDiscountPercentage();
            }
        }

        if (coupon.getType() == CouponType.FIXED_AMOUNT_DISCOUNT) {
            if (ownedCoupon != null && ownedCoupon.getSnapshotDiscountAmount() != null) {
                return ownedCoupon.getSnapshotDiscountAmount();
            }
            CouponDiscountDetails discountDetails = coupon.getDiscountDetails();
            if (discountDetails != null) {
                return discountDetails.getDiscountAmount();
            }
        }

        return null;
    }
}
