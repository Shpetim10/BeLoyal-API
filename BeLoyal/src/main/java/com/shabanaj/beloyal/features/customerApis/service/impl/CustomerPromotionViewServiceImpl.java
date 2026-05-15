package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerPromotionDto;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerApis.service.CustomerPromotionViewService;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.model.Entity.CouponDiscountDetails;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.CustomerCoupon;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerPromotionViewServiceImpl implements CustomerPromotionViewService {

    private final UserService userService;
    private final CustomerProfileService customerProfileService;
    private final CustomerCouponRepository customerCouponRepository;
    private final CouponRepository couponRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerPromotionDto> getPromotions(Long userId, String statusFilter) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        List<CustomerCoupon> ownedCouponEntities = customerCouponRepository
                .findAllWithCouponByCustomerProfileId(customerProfile.getId());

        Map<Long, Integer> redemptionCountByCouponId = ownedCouponEntities.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        cc -> cc.getCoupon().getId(),
                        java.util.stream.Collectors.summingInt(cc -> 1)));

        List<CustomerPromotionDto> ownedPromotions = ownedCouponEntities.stream()
                .map(cc -> toOwnedDto(cc, redemptionCountByCouponId.getOrDefault(cc.getCoupon().getId(), 0)))
                .toList();

        LocalDateTime now = LocalDateTime.now();
        List<CustomerPromotionDto> publicPromotions = couponRepository.findAllActivePublic(now)
                .stream()
                .map(this::toPublicDto)
                .toList();

        Map<Long, CustomerPromotionDto> promotionsByCouponId = new LinkedHashMap<>();
        ownedPromotions.forEach(p -> promotionsByCouponId.put(p.couponId(), p));
        publicPromotions.forEach(p -> promotionsByCouponId.putIfAbsent(p.couponId(), p));
        List<CustomerPromotionDto> promotions = promotionsByCouponId.values().stream().toList();

        if (statusFilter != null && !statusFilter.isBlank()) {
            String filter = statusFilter.toUpperCase();
            return promotions.stream()
                    .filter(p -> p.status().equals(filter))
                    .toList();
        }

        return promotions;
    }

    private CustomerPromotionDto toOwnedDto(CustomerCoupon cc, int customerRedemptionCount) {
        LoyaltyCoupon coupon = cc.getCoupon();
        String status = deriveStatus(cc);
        String discountDisplay = buildDiscountDisplay(cc, coupon);

        //expiration duration calculation
        String expiresIn;
        if(cc.getExpiresAt() != null){
            Duration expirationDuration= Duration.between(LocalDateTime.now(), cc.getExpiresAt());
            long days = expirationDuration.toDays();
            int hours = expirationDuration.toHoursPart();

            expiresIn = days + (days == 1 ? " day" : " days");
            if (hours != 0) {
                expiresIn += " and " + hours + (hours == 1 ? " hour" : " hours");
            }
        }else{
            expiresIn="No expiration date";
        }


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
                coupon.isFeatured(),
                cc.getStatus() == CustomerCouponStatus.USED,
                cc.getStatus() == CustomerCouponStatus.USED ? 1 : 0,
                coupon.getPerCustomerRedemptionLimit(),
                coupon.getTermsAndConditions(),
                true,
                cc.getQrCode(),
                customerRedemptionCount
        );
    }

    private CustomerPromotionDto toPublicDto(LoyaltyCoupon coupon) {
        //expiration duration calculation
        Duration expirationDuration= Duration.between(LocalDateTime.now(), coupon.getEndDate());
        long days = expirationDuration.toDays();
        int hours = expirationDuration.toHoursPart();

        String expiresIn = days + (days == 1 ? " day" : " days");
        if (hours != 0) {
            expiresIn += " and " + hours + (hours == 1 ? " hour" : " hours");
        }
        return new CustomerPromotionDto(
                coupon.getId(),
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
                coupon.isFeatured(),
                false,
                coupon.getTotalRedemptions(),
                coupon.getPerCustomerRedemptionLimit(),
                coupon.getTermsAndConditions(),
                false,
                null,
                0
        );
    }

    private String deriveStatus(CustomerCoupon cc) {
        LocalDateTime now = LocalDateTime.now();
        if (cc.getStatus() == CustomerCouponStatus.REDEEMED) {
            if (cc.getExpiresAt() != null && cc.getExpiresAt().isBefore(now.plusDays(3))) {
                return "EXPIRING";
            }
            return "ACTIVE";
        }
        if (cc.getStatus() == CustomerCouponStatus.USED) {
            return "USED";
        }
        return "EXPIRED";
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
