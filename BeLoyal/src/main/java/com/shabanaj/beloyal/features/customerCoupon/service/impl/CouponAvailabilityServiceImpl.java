package com.shabanaj.beloyal.features.customerCoupon.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.dto.AvailableCouponItem;
import com.shabanaj.beloyal.features.customerCoupon.dto.AvailableCouponsResponse;
import com.shabanaj.beloyal.features.customerCoupon.dto.ValidateRedemptionResponse;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.service.CouponAvailabilityService;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.CouponCannotRedeemCode;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CouponVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponAvailabilityServiceImpl implements CouponAvailabilityService {

    private final CouponRepository couponRepository;
    private final BusinessService businessService;
    private final UserService userService;
    private final CustomerProfileService customerProfileService;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final CustomerCouponRepository customerCouponRepository;

    @Override
    @Transactional(readOnly = true)
    public AvailableCouponsResponse getAvailableCoupons(Long businessId, Long userId) {
        Business business = businessService.getActiveBusinessById(businessId);
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        int balance = loyaltyAccountRepository
                .findByCustomerProfileAndBusiness(customerProfile, business)
                .map(LoyaltyAccount::getAvailablePoints)
                .orElse(0);

        LocalDateTime now = LocalDateTime.now();
        List<LoyaltyCoupon> coupons = couponRepository.findAvailableForBusiness(businessId, now);

        List<AvailableCouponItem> items = coupons.stream().map(c -> {
            String cannotRedeemReason = null;
            CouponCannotRedeemCode cannotRedeemCode = null;
            boolean canRedeem = true;

            int customerUsed = customerCouponRepository.countByCouponIdAndCustomerProfileId(c.getId(), customerProfile.getId());

            if (c.isSoldOut()) {
                canRedeem = false;
                cannotRedeemCode = CouponCannotRedeemCode.SOLD_OUT;
                cannotRedeemReason = "Sold out";
            } else if (c.getPerCustomerRedemptionLimit() != null && customerUsed >= c.getPerCustomerRedemptionLimit()) {
                canRedeem = false;
                cannotRedeemCode = CouponCannotRedeemCode.PER_CUSTOMER_LIMIT;
                cannotRedeemReason = "Personal limit reached";
            } else if (balance < c.getPointsCost()) {
                canRedeem = false;
                cannotRedeemCode = CouponCannotRedeemCode.INSUFFICIENT_POINTS;
                cannotRedeemReason = "Insufficient points";
            }

            String displayStatus = c.getEndDate().isBefore(now.plusDays(3)) ? "EXPIRING" : "ACTIVE";
            String discountDisplay = buildDiscountDisplay(c);
            BigDecimal discountValue = extractDiscountValue(c);

            return AvailableCouponItem.builder()
                    .couponId(c.getId())
                    .type(c.getType())
                    .title(c.getTitle())
                    .description(c.getDescription())
                    .imageUrl(c.getImageUrl())
                    .pointCost(c.getPointsCost())
                    .currency(c.getCurrency())
                    .displayStatus(displayStatus)
                    .discountDisplay(discountDisplay)
                    .discountValue(discountValue)
                    .startDate(c.getStartDate())
                    .expiresAt(c.getEndDate())
                    .totalRedemptionLimit(c.getTotalRedemptionLimit())
                    .totalRedemptions(c.getTotalRedemptions())
                    .perCustomerRedemptionLimit(c.getPerCustomerRedemptionLimit())
                    .termsAndConditions(c.getTermsAndConditions())
                    .isFeatured(c.isFeatured())
                    .customerRedemptionCount(customerUsed)
                    .canRedeem(canRedeem)
                    .cannotRedeemReason(cannotRedeemReason)
                    .cannotRedeemCode(cannotRedeemCode)
                    .build();
        }).toList();

        return AvailableCouponsResponse.builder()
                .customerPointBalance(balance)
                .businessCurrency(business.getCurrencyCode())
                .coupons(items)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateRedemptionResponse validateRedemption(Long couponId, Long userId) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        LoyaltyCoupon coupon = couponRepository.findById(couponId).orElse(null);

        if (coupon == null || coupon.isDeleted()) {
            return ValidateRedemptionResponse.builder()
                    .canRedeem(false).reason("Coupon not found").build();
        }

        LocalDateTime now = LocalDateTime.now();

        if (coupon.getStatus() != CouponStatus.ACTIVE || coupon.getVisibility() != CouponVisibility.PUBLIC) {
            return ValidateRedemptionResponse.builder()
                    .canRedeem(false).reason("Coupon is not active")
                    .pointsRequired(coupon.getPointsCost()).build();
        }

        if (!coupon.isWithinDateRange(now)) {
            return ValidateRedemptionResponse.builder()
                    .canRedeem(false).reason("Coupon is outside its validity period")
                    .pointsRequired(coupon.getPointsCost()).build();
        }

        if (coupon.isSoldOut()) {
            return ValidateRedemptionResponse.builder()
                    .canRedeem(false).reason("Coupon is sold out")
                    .pointsRequired(coupon.getPointsCost()).build();
        }

        int balance = loyaltyAccountRepository
                .findByCustomerProfileAndBusiness(customerProfile, coupon.getBusiness())
                .map(LoyaltyAccount::getAvailablePoints)
                .orElse(0);

        if (balance < coupon.getPointsCost()) {
            return ValidateRedemptionResponse.builder()
                    .canRedeem(false)
                    .reason("Insufficient points. Required: " + coupon.getPointsCost() + ", available: " + balance)
                    .pointsRequired(coupon.getPointsCost())
                    .customerBalance(balance)
                    .build();
        }

        if (coupon.getPerCustomerRedemptionLimit() != null) {
            int used = customerCouponRepository.countByCouponIdAndCustomerProfileId(couponId, customerProfile.getId());
            if (used >= coupon.getPerCustomerRedemptionLimit()) {
                return ValidateRedemptionResponse.builder()
                        .canRedeem(false).reason("Redemption limit reached")
                        .pointsRequired(coupon.getPointsCost()).customerBalance(balance).build();
            }
        }

        return ValidateRedemptionResponse.builder()
                .canRedeem(true)
                .pointsRequired(coupon.getPointsCost())
                .customerBalance(balance)
                .build();
    }

    private String buildDiscountDisplay(LoyaltyCoupon coupon) {
        if (coupon.getType() == CouponType.FREE_PRODUCT) return "Free Product";

        CouponDiscountDetails d = coupon.getDiscountDetails();
        if (d == null) return null;

        if (coupon.getType() == CouponType.PERCENTAGE_DISCOUNT && d.getDiscountPercentage() != null) {
            return d.getDiscountPercentage().stripTrailingZeros().toPlainString() + "% Off";
        }
        if (coupon.getType() == CouponType.FIXED_AMOUNT_DISCOUNT && d.getDiscountAmount() != null) {
            return d.getDiscountAmount().stripTrailingZeros().toPlainString()
                    + " " + coupon.getCurrency().getSymbol() + " Off";
        }
        return null;
    }

    private BigDecimal extractDiscountValue(LoyaltyCoupon coupon) {
        CouponDiscountDetails d = coupon.getDiscountDetails();
        if (d == null) return null;
        return switch (coupon.getType()) {
            case PERCENTAGE_DISCOUNT -> d.getDiscountPercentage();
            case FIXED_AMOUNT_DISCOUNT -> d.getDiscountAmount();
            case FREE_PRODUCT -> null;
        };
    }
}
