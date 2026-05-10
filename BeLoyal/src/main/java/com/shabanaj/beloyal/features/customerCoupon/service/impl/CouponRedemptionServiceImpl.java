package com.shabanaj.beloyal.features.customerCoupon.service.impl;

import com.shabanaj.beloyal.common.Exception.*;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.dto.CouponRedeemResponse;
import com.shabanaj.beloyal.features.customerCoupon.dto.CustomerCouponDetailResponse;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.service.CouponRedemptionService;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import com.shabanaj.beloyal.model.Enums.PointsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponRedemptionServiceImpl implements CouponRedemptionService {

    private final CouponRepository couponRepository;
    private final CustomerCouponRepository customerCouponRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final PointsTransactionRepository pointsTransactionRepository;
    private final UserService userService;
    private final CustomerProfileService customerProfileService;

    @Override
    @Transactional
    public CouponRedeemResponse redeem(Long couponId, Long userId) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        // Lock coupon with optimistic version check
        LoyaltyCoupon coupon = couponRepository.findWithLockByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new CouponNotFound(couponId));

        LocalDateTime now = LocalDateTime.now();

        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            throw new CouponNotActiveException();
        }

        if (!coupon.isWithinDateRange(now)) {
            throw new CouponExpiredException();
        }

        if (coupon.isSoldOut()) {
            throw new CouponSoldOutException();
        }

        if (coupon.getPerCustomerRedemptionLimit() != null) {
            int used = customerCouponRepository.countByCouponIdAndCustomerProfileId(couponId, customerProfile.getId());
            if (used >= coupon.getPerCustomerRedemptionLimit()) {
                throw new CustomerRedemptionLimitReachedException();
            }
        }

        // Pessimistic lock on loyalty account
        LoyaltyAccount loyaltyAccount = loyaltyAccountRepository
                .findWithLockByCustomerProfileIdAndBusinessId(customerProfile.getId(), coupon.getBusiness().getId())
                .orElseThrow(() -> new LoyaltyAccountNotFound("No loyalty account found for this business"));

        int balance = loyaltyAccount.getAvailablePoints();
        if (balance < coupon.getPointsCost()) {
            throw new InsufficientPointsException(balance, coupon.getPointsCost());
        }

        // Deduct points
        loyaltyAccount.spend(coupon.getPointsCost());
        loyaltyAccountRepository.save(loyaltyAccount);

        // Increment redemptions count
        coupon.setTotalRedemptions(coupon.getTotalRedemptions() + 1);
        couponRepository.save(coupon);

        // Audit record — stored in DB but excluded from existing bill-based transaction views
        PointsTransaction auditRecord = PointsTransaction.builder()
                .loyaltyAccount(loyaltyAccount)
                .type(PointsType.COUPON_REDEMPTION)
                .pointsDelta(-coupon.getPointsCost())
                .description("Coupon redeemed: " + coupon.getTitle())
                .build();
        pointsTransactionRepository.save(auditRecord);

        // Create customer coupon with snapshot
        CustomerCoupon customerCoupon = buildCustomerCoupon(coupon, customerProfile, now);
        customerCoupon = customerCouponRepository.save(customerCoupon);

        return CouponRedeemResponse.builder()
                .customerCouponId(customerCoupon.getId())
                .couponId(coupon.getId())
                .status(customerCoupon.getStatus())
                .pointsSpent(coupon.getPointsCost())
                .remainingBalance(loyaltyAccount.getAvailablePoints())
                .currency(coupon.getCurrency())
                .redeemedAt(customerCoupon.getRedeemedAt())
                .expiresAt(customerCoupon.getExpiresAt())
                .snapshotTitle(customerCoupon.getSnapshotTitle())
                .snapshotDescription(customerCoupon.getSnapshotDescription())
                .snapshotImageUrl(customerCoupon.getSnapshotImageUrl())
                .snapshotCouponType(customerCoupon.getSnapshotCouponType())
                .build();
    }

    private CustomerCoupon buildCustomerCoupon(LoyaltyCoupon coupon, CustomerProfile customerProfile, LocalDateTime now) {
        CustomerCoupon.CustomerCouponBuilder builder = CustomerCoupon.builder()
                .coupon(coupon)
                .business(coupon.getBusiness())
                .customerProfile(customerProfile)
                .status(CustomerCouponStatus.REDEEMED)
                .redeemedAt(now)
                .pointsSpent(coupon.getPointsCost())
                .currency(coupon.getCurrency())
                .snapshotTitle(coupon.getTitle())
                .snapshotDescription(coupon.getDescription())
                .snapshotImageUrl(coupon.getImageUrl())
                .snapshotCouponType(coupon.getType());

        if (coupon.getType() == CouponType.FREE_PRODUCT && coupon.getFreeProductDetails() != null) {
            CouponFreeProductDetails d = coupon.getFreeProductDetails();
            builder.snapshotProductId(d.getProduct().getId());
            builder.snapshotVariantId(d.getVariant() != null ? d.getVariant().getId() : null);
        } else if (coupon.getDiscountDetails() != null) {
            CouponDiscountDetails d = coupon.getDiscountDetails();
            builder.snapshotDiscountPercentage(d.getDiscountPercentage());
            builder.snapshotDiscountAmount(d.getDiscountAmount());
            builder.snapshotMinimumOrderAmount(d.getMinimumOrderAmount());
            builder.snapshotMaximumDiscountAmount(d.getMaximumDiscountAmount());
        }

        return builder.build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerCouponDetailResponse> getCustomerCoupons(Long userId) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        return customerCouponRepository
                .findAllByCustomerProfileIdOrderByCreatedAtDesc(customerProfile.getId())
                .stream()
                .map(this::toDetailResponse)
                .toList();
    }

    @Override
    public Integer getCustomerCouponsCount(CustomerProfile customerProfile) {
        if(customerProfile == null){
            throw new IllegalArgumentException("CustomerProfile cannot be null");
        }

        return Math.toIntExact(
                customerCouponRepository.countAllByCustomerProfileAndStatus(
                        customerProfile,
                        CustomerCouponStatus.REDEEMED
                )
        );
    }

    @Override
    @Transactional
    public CustomerCouponDetailResponse applyCoupon(Long customerCouponId, Long userId, String orderId) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        CustomerCoupon customerCoupon = customerCouponRepository
                .findByIdAndCustomerProfileId(customerCouponId, customerProfile.getId())
                .orElseThrow(() -> new CustomerCouponNotFound(customerCouponId));

        if (customerCoupon.getStatus() != CustomerCouponStatus.REDEEMED) {
            throw new InvalidCouponOperationException("Coupon cannot be applied — current status: " + customerCoupon.getStatus());
        }

        customerCoupon.setOrderId(orderId);
        customerCoupon = customerCouponRepository.save(customerCoupon);
        return toDetailResponse(customerCoupon);
    }

    @Override
    @Transactional
    public CustomerCouponDetailResponse useCoupon(Long customerCouponId, Long userId) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        CustomerCoupon customerCoupon = customerCouponRepository
                .findByIdAndCustomerProfileId(customerCouponId, customerProfile.getId())
                .orElseThrow(() -> new CustomerCouponNotFound(customerCouponId));

        if (customerCoupon.getStatus() != CustomerCouponStatus.REDEEMED) {
            throw new InvalidCouponOperationException("Coupon cannot be marked as used — current status: " + customerCoupon.getStatus());
        }

        customerCoupon.setStatus(CustomerCouponStatus.USED);
        customerCoupon.setUsedAt(LocalDateTime.now());
        customerCoupon = customerCouponRepository.save(customerCoupon);
        return toDetailResponse(customerCoupon);
    }

    private CustomerCouponDetailResponse toDetailResponse(CustomerCoupon cc) {
        return CustomerCouponDetailResponse.builder()
                .id(cc.getId())
                .couponId(cc.getCoupon().getId())
                .businessId(cc.getBusiness().getId())
                .status(cc.getStatus())
                .pointsSpent(cc.getPointsSpent())
                .currency(cc.getCurrency())
                .redeemedAt(cc.getRedeemedAt())
                .usedAt(cc.getUsedAt())
                .expiresAt(cc.getExpiresAt())
                .orderId(cc.getOrderId())
                .snapshotTitle(cc.getSnapshotTitle())
                .snapshotDescription(cc.getSnapshotDescription())
                .snapshotImageUrl(cc.getSnapshotImageUrl())
                .snapshotCouponType(cc.getSnapshotCouponType())
                .snapshotProductId(cc.getSnapshotProductId())
                .snapshotVariantId(cc.getSnapshotVariantId())
                .snapshotDiscountPercentage(cc.getSnapshotDiscountPercentage())
                .snapshotDiscountAmount(cc.getSnapshotDiscountAmount())
                .snapshotMinimumOrderAmount(cc.getSnapshotMinimumOrderAmount())
                .snapshotMaximumDiscountAmount(cc.getSnapshotMaximumDiscountAmount())
                .createdAt(cc.getCreatedAt())
                .build();
    }
}
