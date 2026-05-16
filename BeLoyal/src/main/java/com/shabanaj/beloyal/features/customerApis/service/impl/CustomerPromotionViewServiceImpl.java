package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerPromotionDto;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerApis.service.CustomerPromotionViewService;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.model.Entity.CouponDiscountDetails;
import com.shabanaj.beloyal.model.Entity.CouponFreeProductDetails;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.CustomerCoupon;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.CouponCannotRedeemCode;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerPromotionViewServiceImpl implements CustomerPromotionViewService {

    private final UserService userService;
    private final CustomerProfileService customerProfileService;
    private final CustomerCouponRepository customerCouponRepository;
    private final CouponRepository couponRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerPromotionDto> getPromotions(Long userId, String statusFilter) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        // Build balance map: businessId → availablePoints
        Map<Long, Integer> balanceByBusiness = loyaltyAccountRepository
                .findAllWithBusinessByCustomerProfileId(customerProfile.getId())
                .stream()
                .collect(Collectors.toMap(
                        la -> la.getBusiness().getId(),
                        LoyaltyAccount::getAvailablePoints));

        LocalDateTime filterNow = LocalDateTime.now();
        List<CustomerCoupon> ownedCouponEntities = customerCouponRepository
                .findAllWithCouponByCustomerProfileId(customerProfile.getId())
                .stream()
                .filter(cc -> !isExpired(cc, filterNow))
                .toList();

        Map<Long, Integer> redemptionCountByCouponId = ownedCouponEntities.stream()
                .collect(Collectors.groupingBy(
                        cc -> cc.getCoupon().getId(),
                        Collectors.summingInt(cc -> 1)));

        List<CustomerPromotionDto> ownedPromotions = ownedCouponEntities.stream()
                .map(cc -> toOwnedDto(cc,
                        redemptionCountByCouponId.getOrDefault(cc.getCoupon().getId(), 0),
                        balanceByBusiness))
                .toList();

        LocalDateTime now = LocalDateTime.now();
        List<CustomerPromotionDto> publicPromotions = couponRepository.findAllActivePublic(now)
                .stream()
                .map(c -> toPublicDto(c,
                        balanceByBusiness.getOrDefault(c.getBusiness().getId(), 0),
                        redemptionCountByCouponId.getOrDefault(c.getId(), 0)))
                .toList();

        // Owned coupons are keyed by customerCouponId (a unique row per redemption).
        // Public/unowned coupons are de-duplicated by couponId, and only added when
        // there is no owned row for that coupon template.
        Set<Long> ownedTemplateCouponIds = ownedCouponEntities.stream()
                .map(cc -> cc.getCoupon().getId())
                .collect(Collectors.toSet());

        List<CustomerPromotionDto> promotions = new ArrayList<>(ownedPromotions);
        publicPromotions.stream()
                .filter(p -> !ownedTemplateCouponIds.contains(p.couponId()))
                .forEach(promotions::add);

        if (statusFilter != null && !statusFilter.isBlank()) {
            String filter = statusFilter.toUpperCase();
            return promotions.stream()
                    .filter(p -> p.status().equals(filter))
                    .toList();
        }

        return promotions;
    }

    private CustomerPromotionDto toOwnedDto(CustomerCoupon cc, int customerRedemptionCount,
                                             Map<Long, Integer> balanceByBusiness) {
        LoyaltyCoupon coupon = cc.getCoupon();
        String status = deriveStatus(cc);
        String discountDisplay = buildDiscountDisplay(cc, coupon);

        String expiresIn;
        if (cc.getExpiresAt() != null) {
            Duration expirationDuration = Duration.between(LocalDateTime.now(), cc.getExpiresAt());
            long days = expirationDuration.toDays();
            int hours = expirationDuration.toHoursPart();
            expiresIn = days + (days == 1 ? " day" : " days");
            if (hours != 0) {
                expiresIn += " and " + hours + (hours == 1 ? " hour" : " hours");
            }
        } else {
            expiresIn = "No expiration date";
        }

        // canRedeem = can the customer claim another copy of this coupon template
        int balance = balanceByBusiness.getOrDefault(cc.getBusiness().getId(), 0);
        boolean canRedeem = true;
        String cannotRedeemReason = null;
        CouponCannotRedeemCode cannotRedeemCode = null;

        if (coupon.getStatus() != com.shabanaj.beloyal.model.Enums.CouponStatus.ACTIVE) {
            canRedeem = false;
            cannotRedeemCode = CouponCannotRedeemCode.TEMPLATE_INACTIVE;
            cannotRedeemReason = "Coupon no longer available";
        } else if (coupon.isSoldOut()) {
            canRedeem = false;
            cannotRedeemCode = CouponCannotRedeemCode.SOLD_OUT;
            cannotRedeemReason = "Sold out";
        } else if (coupon.getPerCustomerRedemptionLimit() != null
                && customerRedemptionCount >= coupon.getPerCustomerRedemptionLimit()) {
            canRedeem = false;
            cannotRedeemCode = CouponCannotRedeemCode.PER_CUSTOMER_LIMIT;
            cannotRedeemReason = "Personal limit reached";
        } else if (balance < coupon.getPointsCost()) {
            canRedeem = false;
            cannotRedeemCode = CouponCannotRedeemCode.INSUFFICIENT_POINTS;
            cannotRedeemReason = "Insufficient points";
        }

        CouponDiscountDetails ownedDiscountDetails = coupon.getDiscountDetails();
        BigDecimal discountValue = resolveDiscountValue(coupon, cc.getSnapshotDiscountPercentage(),
                cc.getSnapshotDiscountAmount(), ownedDiscountDetails);
        BigDecimal minimumOrderAmount = cc.getSnapshotMinimumOrderAmount() != null
                ? cc.getSnapshotMinimumOrderAmount()
                : (ownedDiscountDetails != null ? ownedDiscountDetails.getMinimumOrderAmount() : null);
        BigDecimal maximumDiscountAmount = cc.getSnapshotMaximumDiscountAmount() != null
                ? cc.getSnapshotMaximumDiscountAmount()
                : (ownedDiscountDetails != null ? ownedDiscountDetails.getMaximumDiscountAmount() : null);
        CouponFreeProductDetails ownedFpd = coupon.getFreeProductDetails();
        String imageUrl = cc.getSnapshotImageUrl() != null ? cc.getSnapshotImageUrl() : coupon.getImageUrl();
        String currency = cc.getCurrency() != null ? cc.getCurrency().getSymbol() : coupon.getCurrency().getSymbol();

        return new CustomerPromotionDto(
                cc.getId(),
                cc.getBusiness().getId(),
                coupon.getId(),
                cc.getBusiness().getBusinessName(),
                cc.getSnapshotTitle(),
                cc.getSnapshotDescription(),
                coupon.getType().name(),
                status,
                discountDisplay,
                cc.getPointsSpent(),
                cc.getExpiresAt(),
                expiresIn,
                null,
                false,
                cc.getStatus() == CustomerCouponStatus.USED,
                cc.getStatus() == CustomerCouponStatus.USED ? 1 : 0,
                coupon.getPerCustomerRedemptionLimit(),
                coupon.getTermsAndConditions(),
                true,
                cc.getQrCode(),
                customerRedemptionCount,
                canRedeem,
                cannotRedeemReason,
                coupon.isFeatured(),
                coupon.getTotalRedemptions(),
                coupon.getTotalRedemptionLimit(),
                cannotRedeemCode,
                imageUrl,
                coupon.getType().name(),
                discountValue,
                currency,
                coupon.getStartDate(),
                minimumOrderAmount,
                maximumDiscountAmount,
                ownedFpd != null ? ownedFpd.getCategory().getId() : null,
                ownedFpd != null ? ownedFpd.getCategory().getName() : null,
                ownedFpd != null ? ownedFpd.getProduct().getId() : null,
                ownedFpd != null ? ownedFpd.getProduct().getName() : null,
                ownedFpd != null && ownedFpd.getVariant() != null ? ownedFpd.getVariant().getId() : null,
                ownedFpd != null && ownedFpd.getVariant() != null ? ownedFpd.getVariant().getName() : null,
                ownedFpd != null ? ownedFpd.getQuantity() : null,
                cc.getSnapshotTitle(),
                cc.getSnapshotDescription(),
                cc.getSnapshotImageUrl(),
                cc.getSnapshotCouponType() != null ? cc.getSnapshotCouponType().name() : null,
                cc.getSnapshotMinimumOrderAmount(),
                cc.getSnapshotMaximumDiscountAmount(),
                cc.getRedeemedAt(),
                cc.getUsedAt(),
                cc.getOrderId()
        );
    }

    private CustomerPromotionDto toPublicDto(LoyaltyCoupon coupon, int balance, int customerRedemptionCount) {
        Duration expirationDuration = Duration.between(LocalDateTime.now(), coupon.getEndDate());
        long days = expirationDuration.toDays();
        int hours = expirationDuration.toHoursPart();
        String expiresIn = days + (days == 1 ? " day" : " days");
        if (hours != 0) {
            expiresIn += " and " + hours + (hours == 1 ? " hour" : " hours");
        }

        boolean canRedeem = true;
        String cannotRedeemReason = null;
        CouponCannotRedeemCode cannotRedeemCode = null;
        if (coupon.isSoldOut()) {
            canRedeem = false;
            cannotRedeemCode = CouponCannotRedeemCode.SOLD_OUT;
            cannotRedeemReason = "Sold out";
        } else if (coupon.getPerCustomerRedemptionLimit() != null
                && customerRedemptionCount >= coupon.getPerCustomerRedemptionLimit()) {
            canRedeem = false;
            cannotRedeemCode = CouponCannotRedeemCode.PER_CUSTOMER_LIMIT;
            cannotRedeemReason = "Personal limit reached";
        } else if (balance < coupon.getPointsCost()) {
            canRedeem = false;
            cannotRedeemCode = CouponCannotRedeemCode.INSUFFICIENT_POINTS;
            cannotRedeemReason = "Insufficient points";
        }

        CouponDiscountDetails publicDiscountDetails = coupon.getDiscountDetails();
        BigDecimal publicDiscountValue = resolveDiscountValue(coupon,
                publicDiscountDetails != null ? publicDiscountDetails.getDiscountPercentage() : null,
                publicDiscountDetails != null ? publicDiscountDetails.getDiscountAmount() : null,
                publicDiscountDetails);
        CouponFreeProductDetails publicFpd = coupon.getFreeProductDetails();

        return new CustomerPromotionDto(
                null,           // id = null for public/unowned coupons; couponId is the identifier
                coupon.getBusiness().getId(),
                coupon.getId(),
                coupon.getBusiness().getBusinessName(),
                coupon.getTitle(),
                coupon.getDescription(),
                coupon.getType().name(),
                "ACTIVE",
                buildDiscountDisplay(coupon),
                coupon.getPointsCost(),
                coupon.getEndDate(),
                expiresIn,
                null,
                false,
                false,
                0,  // usageCount: 0 for public/unowned rows — this customer has not used this coupon
                coupon.getPerCustomerRedemptionLimit(),
                coupon.getTermsAndConditions(),
                false,
                null,
                customerRedemptionCount,
                canRedeem,
                cannotRedeemReason,
                coupon.isFeatured(),
                coupon.getTotalRedemptions(),
                coupon.getTotalRedemptionLimit(),
                cannotRedeemCode,
                coupon.getImageUrl(),
                coupon.getType().name(),
                publicDiscountValue,
                coupon.getCurrency().getSymbol(),
                coupon.getStartDate(),
                publicDiscountDetails != null ? publicDiscountDetails.getMinimumOrderAmount() : null,
                publicDiscountDetails != null ? publicDiscountDetails.getMaximumDiscountAmount() : null,
                publicFpd != null ? publicFpd.getCategory().getId() : null,
                publicFpd != null ? publicFpd.getCategory().getName() : null,
                publicFpd != null ? publicFpd.getProduct().getId() : null,
                publicFpd != null ? publicFpd.getProduct().getName() : null,
                publicFpd != null && publicFpd.getVariant() != null ? publicFpd.getVariant().getId() : null,
                publicFpd != null && publicFpd.getVariant() != null ? publicFpd.getVariant().getName() : null,
                publicFpd != null ? publicFpd.getQuantity() : null,
                null, null, null, null, null, null,
                null, null, null
        );
    }

    private boolean isExpired(CustomerCoupon cc, LocalDateTime now) {
        if (cc.getStatus() == CustomerCouponStatus.EXPIRED) return true;
        return cc.getStatus() == CustomerCouponStatus.REDEEMED
                && cc.getExpiresAt() != null
                && cc.getExpiresAt().isBefore(now);
    }

    private String deriveStatus(CustomerCoupon cc) {
        LocalDateTime now = LocalDateTime.now();
        if (cc.getStatus() == CustomerCouponStatus.REDEEMED) {
            if (cc.getExpiresAt() != null && cc.getExpiresAt().isBefore(now)) {
                return "EXPIRED";
            }
            if (cc.getExpiresAt() != null && cc.getExpiresAt().isBefore(now.plusDays(3))) {
                return "EXPIRING";
            }
            return "ACTIVE";
        }
        if (cc.getStatus() == CustomerCouponStatus.USED) {
            return "USED";
        }
        if (cc.getStatus() == CustomerCouponStatus.CANCELLED) {
            return "CANCELLED";
        }
        return "EXPIRED";
    }

    private BigDecimal resolveDiscountValue(LoyaltyCoupon coupon, BigDecimal pct, BigDecimal amt, CouponDiscountDetails details) {
        return switch (coupon.getType()) {
            case PERCENTAGE_DISCOUNT -> pct != null ? pct : (details != null ? details.getDiscountPercentage() : null);
            case FIXED_AMOUNT_DISCOUNT -> amt != null ? amt : (details != null ? details.getDiscountAmount() : null);
            case FREE_PRODUCT -> null;
        };
    }

    private String buildDiscountDisplay(CustomerCoupon cc, LoyaltyCoupon coupon) {
        if (coupon.getType() == CouponType.FREE_PRODUCT) {
            return "Free Product";
        }
        if (coupon.getType() == CouponType.PERCENTAGE_DISCOUNT && cc.getSnapshotDiscountPercentage() != null) {
            return cc.getSnapshotDiscountPercentage().stripTrailingZeros().toPlainString() + "% Off";
        }
        if (coupon.getType() == CouponType.FIXED_AMOUNT_DISCOUNT && cc.getSnapshotDiscountAmount() != null) {
            return cc.getSnapshotDiscountAmount().stripTrailingZeros().toPlainString()
                    + " " + cc.getCurrency().name() + " Off";
        }
        return null;
    }

    private String buildDiscountDisplay(LoyaltyCoupon coupon) {
        if (coupon.getType() == CouponType.FREE_PRODUCT) {
            return "Free Product";
        }

        CouponDiscountDetails discountDetails = coupon.getDiscountDetails();
        if (discountDetails == null) {
            return null;
        }

        if (coupon.getType() == CouponType.PERCENTAGE_DISCOUNT && discountDetails.getDiscountPercentage() != null) {
            return discountDetails.getDiscountPercentage().stripTrailingZeros().toPlainString() + "% Off";
        }

        if (coupon.getType() == CouponType.FIXED_AMOUNT_DISCOUNT && discountDetails.getDiscountAmount() != null) {
            return discountDetails.getDiscountAmount().stripTrailingZeros().toPlainString()
                    + " " + coupon.getCurrency().name() + " Off";
        }

        return null;
    }
}
