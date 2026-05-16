package com.shabanaj.beloyal.features.customerCoupon.service.impl;

import com.shabanaj.beloyal.common.Exception.*;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberService;
import com.shabanaj.beloyal.features.customerCoupon.dto.StaffCouponScanRequest;
import com.shabanaj.beloyal.features.customerCoupon.dto.StaffCouponScanResponse;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.service.StaffCouponRedemptionService;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import com.shabanaj.beloyal.model.Enums.RedemptionChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StaffCouponRedemptionServiceImpl implements StaffCouponRedemptionService {

    private final CustomerCouponRepository customerCouponRepository;
    private final BusinessMemberService businessMemberService;
    private final UserService userService;

    @Override
    @Transactional
    public StaffCouponScanResponse scanAndRedeem(Long businessId, Long staffUserId, StaffCouponScanRequest request) {
        User staffUser = userService.getUserOrThrow(staffUserId);
        BusinessMember staffMember = businessMemberService.getBusinessMemberByUserIdAndBusinessId(staffUserId, businessId);

        CustomerCoupon customerCoupon = customerCouponRepository
                .findWithLockByQrCode(request.getQrCode())
                .orElseThrow(InvalidQrCodeException::new);

        if (!customerCoupon.getBusiness().getId().equals(businessId)) {
            throw new InvalidQrCodeException();
        }

        LoyaltyCoupon coupon = customerCoupon.getCoupon();

        if (coupon.getType() != CouponType.FREE_PRODUCT) {
            throw new CouponTypeMismatchException("This endpoint only handles FREE_PRODUCT coupons. Use the earn-points endpoint for discount coupons.");
        }

        if (customerCoupon.getStatus() == CustomerCouponStatus.USED) {
            throw new CouponAlreadyUsedException();
        }

        if (customerCoupon.getStatus() != CustomerCouponStatus.REDEEMED) {
            throw new InvalidCouponOperationException("Customer coupon is not in a redeemable state: " + customerCoupon.getStatus());
        }

        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            throw new CouponNotActiveException();
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(coupon.getStartDate())) {
            throw new CouponNotYetValidException();
        }

        // Owned coupons expire from the redemption snapshot, so later edits to the
        // coupon template endDate do not retroactively invalidate a purchased row.
        LocalDateTime effectiveExpiry = customerCoupon.getExpiresAt() != null
                ? customerCoupon.getExpiresAt()
                : coupon.getEndDate();

        if (effectiveExpiry != null && now.isAfter(effectiveExpiry)) {
            throw new CouponExpiredException();
        }

        customerCoupon.setStatus(CustomerCouponStatus.USED);
        customerCoupon.setUsedAt(now);
        customerCoupon.setRedeemedByStaff(staffMember);
        customerCoupon.setRedemptionLocation(request.getRedemptionLocation());
        customerCoupon.setRedemptionChannel(RedemptionChannel.STAFF_SCAN);
        customerCouponRepository.save(customerCoupon);

        return StaffCouponScanResponse.builder()
                .customerCouponId(customerCoupon.getId())
                .couponId(coupon.getId())
                .status(customerCoupon.getStatus())
                .couponType(coupon.getType())
                .couponTitle(customerCoupon.getSnapshotTitle())
                .snapshotProductId(customerCoupon.getSnapshotProductId())
                .snapshotVariantId(customerCoupon.getSnapshotVariantId())
                .usedAt(customerCoupon.getUsedAt())
                .redemptionLocation(customerCoupon.getRedemptionLocation())
                .build();
    }
}
