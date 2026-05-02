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
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            boolean canRedeem = true;

            if (balance < c.getPointsCost()) {
                canRedeem = false;
                cannotRedeemReason = "Insufficient points";
            } else if (c.getPerCustomerRedemptionLimit() != null) {
                int used = customerCouponRepository.countByCouponIdAndCustomerProfileId(c.getId(), customerProfile.getId());
                if (used >= c.getPerCustomerRedemptionLimit()) {
                    canRedeem = false;
                    cannotRedeemReason = "Redemption limit reached";
                }
            }

            return AvailableCouponItem.builder()
                    .couponId(c.getId())
                    .type(c.getType())
                    .title(c.getTitle())
                    .description(c.getDescription())
                    .imageUrl(c.getImageUrl())
                    .pointsCost(c.getPointsCost())
                    .currency(c.getCurrency())
                    .startDate(c.getStartDate())
                    .endDate(c.getEndDate())
                    .totalRedemptionLimit(c.getTotalRedemptionLimit())
                    .totalRedemptions(c.getTotalRedemptions())
                    .perCustomerRedemptionLimit(c.getPerCustomerRedemptionLimit())
                    .termsAndConditions(c.getTermsAndConditions())
                    .isFeatured(c.isFeatured())
                    .canRedeem(canRedeem)
                    .cannotRedeemReason(cannotRedeemReason)
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

        LoyaltyCoupon coupon = couponRepository.findById(couponId)
                .orElse(null);

        if (coupon == null || coupon.isDeleted()) {
            return ValidateRedemptionResponse.builder()
                    .canRedeem(false).reason("Coupon not found").build();
        }

        LocalDateTime now = LocalDateTime.now();

        if (coupon.getStatus() != CouponStatus.ACTIVE) {
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
}
