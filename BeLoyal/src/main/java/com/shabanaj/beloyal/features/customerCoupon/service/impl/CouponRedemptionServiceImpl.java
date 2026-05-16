package com.shabanaj.beloyal.features.customerCoupon.service.impl;

import com.shabanaj.beloyal.common.Exception.*;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.dto.CouponRedeemResponse;
import com.shabanaj.beloyal.features.customerCoupon.dto.CustomerCouponDetailResponse;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.service.CouponRedemptionService;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.pointsBucket.service.PointsBucketService;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.CouponCannotRedeemCode;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CouponVisibility;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import com.shabanaj.beloyal.model.Enums.PointsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponRedemptionServiceImpl implements CouponRedemptionService {

    private final CouponRepository couponRepository;
    private final CustomerCouponRepository customerCouponRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final PointsTransactionRepository pointsTransactionRepository;
    private final PointsBucketService pointsBucketService;
    private final UserService userService;
    private final CustomerProfileService customerProfileService;

    @Override
    @Transactional
    public CouponRedeemResponse redeem(Long couponId, Long userId) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        LoyaltyCoupon coupon = couponRepository.findWithLockByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new CouponNotFound(couponId));

        LocalDateTime now = LocalDateTime.now();

        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            throw new CouponNotActiveException();
        }

        if (coupon.getVisibility() != CouponVisibility.PUBLIC) {
            throw new CouponNotFound(couponId);
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

        LoyaltyAccount loyaltyAccount = loyaltyAccountRepository
                .findWithLockByCustomerProfileIdAndBusinessId(customerProfile.getId(), coupon.getBusiness().getId())
                .orElseThrow(() -> new LoyaltyAccountNotFound("No loyalty account found for this business"));

        int balance = loyaltyAccount.getAvailablePoints();
        if (balance < coupon.getPointsCost()) {
            throw new InsufficientPointsException(balance, coupon.getPointsCost());
        }

        loyaltyAccount.spend(coupon.getPointsCost());
        loyaltyAccountRepository.save(loyaltyAccount);

        coupon.setTotalRedemptions(coupon.getTotalRedemptions() + 1);
        couponRepository.save(coupon);

        PointsTransaction auditRecord = PointsTransaction.builder()
                .loyaltyAccount(loyaltyAccount)
                .type(PointsType.COUPON_REDEMPTION)
                .pointsDelta(-coupon.getPointsCost())
                .description("Coupon redeemed: " + coupon.getTitle())
                .build();
        pointsTransactionRepository.save(auditRecord);

        pointsBucketService.spend(loyaltyAccount.getId(), coupon.getPointsCost(), auditRecord);

        String qrCode = UUID.randomUUID().toString();
        CustomerCoupon customerCoupon = buildCustomerCoupon(coupon, customerProfile, now, qrCode);
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
                .qrCode(customerCoupon.getQrCode())
                .snapshotTitle(customerCoupon.getSnapshotTitle())
                .snapshotDescription(customerCoupon.getSnapshotDescription())
                .snapshotImageUrl(customerCoupon.getSnapshotImageUrl())
                .snapshotCouponType(customerCoupon.getSnapshotCouponType())
                .build();
    }

    private CustomerCoupon buildCustomerCoupon(LoyaltyCoupon coupon, CustomerProfile customerProfile, LocalDateTime now, String qrCode) {
        CustomerCoupon.CustomerCouponBuilder builder = CustomerCoupon.builder()
                .coupon(coupon)
                .business(coupon.getBusiness())
                .customerProfile(customerProfile)
                .status(CustomerCouponStatus.REDEEMED)
                .redeemedAt(now)
                // Snapshot the coupon endDate so the owned row has an explicit expiry.
                // If the coupon had no endDate (open-ended), expiresAt stays null and hasExpiry = false.
                .expiresAt(coupon.getEndDate())
                .pointsSpent(coupon.getPointsCost())
                .currency(coupon.getCurrency())
                .qrCode(qrCode)
                .qrCodeGeneratedAt(now)
                .snapshotTitle(coupon.getTitle())
                .snapshotDescription(coupon.getDescription())
                .snapshotImageUrl(coupon.getImageUrl())
                .snapshotCouponType(coupon.getType());

        if (coupon.getType() == CouponType.FREE_PRODUCT && coupon.getFreeProductDetails() != null) {
            CouponFreeProductDetails d = coupon.getFreeProductDetails();
            builder.snapshotProductId(d.getProduct().getId());
            builder.snapshotVariantId(d.getVariant() != null ? d.getVariant().getId() : null);
            builder.snapshotFreeProductCategory(d.getCategory().getName());
            builder.snapshotFreeProductName(d.getProduct().getName());
            builder.snapshotFreeProductVariant(d.getVariant() != null ? d.getVariant().getName() : null);
            builder.snapshotFreeProductQuantity(d.getQuantity());
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

        List<CustomerCoupon> coupons = customerCouponRepository
                .findAllByCustomerProfileIdOrderByCreatedAtDesc(customerProfile.getId())
                .stream()
                .filter(cc -> cc.getStatus() != CustomerCouponStatus.EXPIRED)
                .toList();

        Map<Long, Integer> balanceByBusinessId = loyaltyAccountRepository
                .findAllWithBusinessByCustomerProfileId(customerProfile.getId())
                .stream()
                .collect(Collectors.toMap(la -> la.getBusiness().getId(), LoyaltyAccount::getAvailablePoints));

        Map<Long, Long> countByCouponId = coupons.stream()
                .collect(Collectors.groupingBy(cc -> cc.getCoupon().getId(), Collectors.counting()));

        return coupons.stream()
                .map(cc -> toDetailResponse(cc,
                        balanceByBusinessId.getOrDefault(cc.getBusiness().getId(), 0),
                        Math.toIntExact(countByCouponId.getOrDefault(cc.getCoupon().getId(), 0L))))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerCouponDetailResponse getCustomerCouponById(Long customerCouponId, Long userId) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        CustomerCoupon cc = customerCouponRepository
                .findByIdAndCustomerProfileId(customerCouponId, customerProfile.getId())
                .orElseThrow(() -> new CustomerCouponNotFound(customerCouponId));

        int balance = loyaltyAccountRepository
                .findByCustomerProfileAndBusiness(customerProfile, cc.getBusiness())
                .map(LoyaltyAccount::getAvailablePoints)
                .orElse(0);
        int count = customerCouponRepository.countByCouponIdAndCustomerProfileId(
                cc.getCoupon().getId(), customerProfile.getId());
        return toDetailResponse(cc, balance, count);
    }

    @Override
    public Integer getCustomerCouponsCount(CustomerProfile customerProfile) {
        if (customerProfile == null) {
            throw new IllegalArgumentException("CustomerProfile cannot be null");
        }

        // Count only REDEEMED coupons whose snapshot expiry has not yet passed.
        // This prevents expired-but-unscanned rows from inflating the active count.
        return Math.toIntExact(
                customerCouponRepository.countActiveByCustomerProfile(
                        customerProfile,
                        CustomerCouponStatus.REDEEMED,
                        LocalDateTime.now()
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
        int balance = loyaltyAccountRepository
                .findByCustomerProfileAndBusiness(customerProfile, customerCoupon.getBusiness())
                .map(LoyaltyAccount::getAvailablePoints)
                .orElse(0);
        int count = customerCouponRepository.countByCouponIdAndCustomerProfileId(
                customerCoupon.getCoupon().getId(), customerProfile.getId());
        return toDetailResponse(customerCoupon, balance, count);
    }

    private CustomerCouponDetailResponse toDetailResponse(CustomerCoupon cc, int balance, int customerRedemptionCount) {
        String title = cc.getSnapshotTitle() != null ? cc.getSnapshotTitle() : cc.getCoupon().getTitle();
        CouponType effectiveType = cc.getSnapshotCouponType() != null ? cc.getSnapshotCouponType() : cc.getCoupon().getType();
        String type = effectiveType != null ? effectiveType.name() : null;
        String displayStatus = deriveDisplayStatus(cc);
        String expiresIn = buildExpiresIn(cc.getExpiresAt());
        LocalDateTime now = LocalDateTime.now();

        // isUsed: this owned instance has been consumed at checkout
        boolean isUsed = cc.getStatus() == CustomerCouponStatus.USED;

        // canUse: this owned instance can be presented at checkout
        boolean canUse;
        String cannotUseReason;
        switch (cc.getStatus()) {
            case USED -> { canUse = false; cannotUseReason = "Already used"; }
            case CANCELLED -> { canUse = false; cannotUseReason = "Cancelled"; }
            case EXPIRED -> { canUse = false; cannotUseReason = "Expired"; }
            case REDEEMED -> {
                if (cc.getExpiresAt() != null && cc.getExpiresAt().isBefore(now)) {
                    canUse = false;
                    cannotUseReason = "Expired";
                } else {
                    canUse = true;
                    cannotUseReason = null;
                }
            }
            default -> { canUse = false; cannotUseReason = null; }
        }

        // discountDisplay and discountValue from snapshot, fallback to live template
        String discountDisplay = buildDiscountDisplay(cc, effectiveType);
        java.math.BigDecimal discountValue = extractDiscountValue(cc, effectiveType);

        // Free-product names: snapshot first, live catalog fallback
        String freeProductCategory = null;
        String freeProductName = null;
        String freeProductVariant = null;
        Integer freeProductQuantity = null;
        if (effectiveType == CouponType.FREE_PRODUCT) {
            freeProductCategory = cc.getSnapshotFreeProductCategory();
            freeProductName = cc.getSnapshotFreeProductName();
            freeProductVariant = cc.getSnapshotFreeProductVariant();
            freeProductQuantity = cc.getSnapshotFreeProductQuantity();
            // Fallback to live catalog if snapshot names are absent (pre-migration rows)
            if (freeProductCategory == null || freeProductName == null) {
                CouponFreeProductDetails fpd = cc.getCoupon().getFreeProductDetails();
                if (fpd != null) {
                    if (freeProductCategory == null) freeProductCategory = fpd.getCategory().getName();
                    if (freeProductName == null) freeProductName = fpd.getProduct().getName();
                    if (freeProductVariant == null && fpd.getVariant() != null) freeProductVariant = fpd.getVariant().getName();
                    if (freeProductQuantity == null) freeProductQuantity = fpd.getQuantity();
                }
            }
        }

        // canRedeem: can the customer buy another copy of this template right now
        LoyaltyCoupon coupon = cc.getCoupon();
        boolean canRedeem = true;
        String cannotRedeemReason = null;
        CouponCannotRedeemCode cannotRedeemCode = null;

        if (coupon.getStatus() != CouponStatus.ACTIVE) {
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

        return CustomerCouponDetailResponse.builder()
                .id(cc.getId())
                .customerCouponId(cc.getId())
                .couponId(coupon.getId())
                .businessId(cc.getBusiness().getId())
                .businessName(cc.getBusiness().getBusinessName())
                .title(title)
                .type(type)
                .displayStatus(displayStatus)
                .customerCouponStatus(cc.getStatus())
                .isUsed(isUsed)
                .discountDisplay(discountDisplay)
                .discountValue(discountValue)
                .freeProductCategory(freeProductCategory)
                .freeProductName(freeProductName)
                .freeProductVariant(freeProductVariant)
                .freeProductQuantity(freeProductQuantity)
                .canUse(canUse)
                .cannotUseReason(cannotUseReason)
                .pointsSpent(cc.getPointsSpent())
                .currency(cc.getCurrency())
                .redeemedAt(cc.getRedeemedAt())
                .usedAt(cc.getUsedAt())
                .expiresAt(cc.getExpiresAt())
                .expiresIn(expiresIn)
                .hasExpiry(cc.getExpiresAt() != null)
                .orderId(cc.getOrderId())
                .qrCode(cc.getQrCode())
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
                .isFeatured(coupon.isFeatured())
                .perCustomerRedemptionLimit(coupon.getPerCustomerRedemptionLimit())
                .totalRedemptions(coupon.getTotalRedemptions())
                .totalRedemptionLimit(coupon.getTotalRedemptionLimit())
                .customerRedemptionCount(customerRedemptionCount)
                .canRedeem(canRedeem)
                .cannotRedeemReason(cannotRedeemReason)
                .cannotRedeemCode(cannotRedeemCode)
                .build();
    }

    private String buildDiscountDisplay(CustomerCoupon cc, CouponType type) {
        if (type == CouponType.FREE_PRODUCT) return "Free Product";
        if (type == CouponType.PERCENTAGE_DISCOUNT && cc.getSnapshotDiscountPercentage() != null) {
            return cc.getSnapshotDiscountPercentage().stripTrailingZeros().toPlainString() + "% Off";
        }
        if (type == CouponType.FIXED_AMOUNT_DISCOUNT && cc.getSnapshotDiscountAmount() != null) {
            String symbol = cc.getCurrency() != null ? " " + cc.getCurrency().getSymbol() : "";
            return cc.getSnapshotDiscountAmount().stripTrailingZeros().toPlainString() + symbol + " Off";
        }
        return null;
    }

    private java.math.BigDecimal extractDiscountValue(CustomerCoupon cc, CouponType type) {
        if (type == CouponType.PERCENTAGE_DISCOUNT) return cc.getSnapshotDiscountPercentage();
        if (type == CouponType.FIXED_AMOUNT_DISCOUNT) return cc.getSnapshotDiscountAmount();
        return null;
    }

    private String deriveDisplayStatus(CustomerCoupon cc) {
        LocalDateTime now = LocalDateTime.now();
        return switch (cc.getStatus()) {
            case USED -> "USED";
            case EXPIRED -> "EXPIRED";
            case CANCELLED -> "CANCELLED";
            case REDEEMED -> {
                if (cc.getExpiresAt() != null && cc.getExpiresAt().isBefore(now)) {
                    yield "EXPIRED";
                }
                if (cc.getExpiresAt() != null && cc.getExpiresAt().isBefore(now.plusDays(3))) {
                    yield "EXPIRING";
                }
                yield "ACTIVE";
            }
        };
    }

    private boolean isExpired(CustomerCoupon cc, LocalDateTime now) {
        if (cc.getStatus() == CustomerCouponStatus.EXPIRED) return true;
        return cc.getStatus() == CustomerCouponStatus.REDEEMED
                && cc.getExpiresAt() != null
                && cc.getExpiresAt().isBefore(now);
    }

    private String buildExpiresIn(LocalDateTime expiresAt) {
        if (expiresAt == null) return null;
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(now, expiresAt);
        if (diff.isNegative()) {
            return "Expired " + Math.abs(diff.toDays()) + "d ago";
        }
        long totalHours = diff.toHours();
        if (totalHours < 24) return "Expires in " + totalHours + "h";
        return "Expires in " + diff.toDays() + "d";
    }
}
