package com.shabanaj.beloyal.features.registerLoyaltyPoints.service.impl;

import com.shabanaj.beloyal.common.Exception.*;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.CouponDiscountResult;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.CouponDiscountCalculatorService;
import com.shabanaj.beloyal.model.Entity.CouponDiscountDetails;
import com.shabanaj.beloyal.model.Entity.CustomerCoupon;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class CouponDiscountCalculatorServiceImpl implements CouponDiscountCalculatorService {

    @Override
    public CouponDiscountResult calculate(CustomerCoupon customerCoupon, BigDecimal billAmount) {
        if (customerCoupon.getStatus() == CustomerCouponStatus.USED) {
            throw new CouponAlreadyUsedException();
        }

        if (customerCoupon.getStatus() != CustomerCouponStatus.REDEEMED) {
            throw new InvalidCouponOperationException("Coupon is not in a redeemable state: " + customerCoupon.getStatus());
        }

        LoyaltyCoupon coupon = customerCoupon.getCoupon();

        if (coupon.getType() == CouponType.FREE_PRODUCT) {
            throw new CouponTypeMismatchException("FREE_PRODUCT coupons cannot be applied to transactions. Use the staff scan endpoint.");
        }

        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            throw new CouponNotActiveException();
        }

        LocalDateTime now = LocalDateTime.now();

        // Use the snapshot expiry captured at redemption time. This prevents business
        // edits to coupon.endDate from retroactively invalidating owned coupons.
        LocalDateTime effectiveExpiry = customerCoupon.getExpiresAt() != null
                ? customerCoupon.getExpiresAt()
                : coupon.getEndDate();

        if (now.isBefore(coupon.getStartDate())) {
            throw new CouponNotYetValidException();
        }

        if (effectiveExpiry != null && now.isAfter(effectiveExpiry)) {
            throw new CouponExpiredException();
        }

        // Prefer snapshot values captured at redemption time; fall back to live details for
        // CustomerCoupon records created before snapshots were introduced.
        CouponDiscountDetails details = coupon.getDiscountDetails();
        BigDecimal discount;

        if (coupon.getType() == CouponType.PERCENTAGE_DISCOUNT) {
            BigDecimal pct = customerCoupon.getSnapshotDiscountPercentage();
            if (pct == null) {
                if (details == null || details.getDiscountPercentage() == null) {
                    throw new InvalidCouponOperationException("Coupon discount details are missing");
                }
                pct = details.getDiscountPercentage();
            }
            BigDecimal minOrderPct = customerCoupon.getSnapshotMinimumOrderAmount() != null
                    ? customerCoupon.getSnapshotMinimumOrderAmount()
                    : (details != null ? details.getMinimumOrderAmount() : null);
            if (minOrderPct != null && billAmount.compareTo(minOrderPct) < 0) {
                throw new InvalidCouponOperationException(
                        "Transaction amount " + billAmount + " is below the minimum required " + minOrderPct + " for this coupon");
            }
            discount = billAmount.multiply(pct).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal maxDiscount = customerCoupon.getSnapshotMaximumDiscountAmount() != null
                    ? customerCoupon.getSnapshotMaximumDiscountAmount()
                    : (details != null ? details.getMaximumDiscountAmount() : null);
            if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
                discount = maxDiscount;
            }
        } else {
            // FIXED_AMOUNT_DISCOUNT
            BigDecimal amt = customerCoupon.getSnapshotDiscountAmount();
            if (amt == null) {
                if (details == null || details.getDiscountAmount() == null) {
                    throw new InvalidCouponOperationException("Coupon discount details are missing");
                }
                amt = details.getDiscountAmount();
            }
            BigDecimal minOrder = customerCoupon.getSnapshotMinimumOrderAmount() != null
                    ? customerCoupon.getSnapshotMinimumOrderAmount()
                    : (details != null ? details.getMinimumOrderAmount() : null);
            if (minOrder != null && billAmount.compareTo(minOrder) < 0) {
                throw new InvalidCouponOperationException(
                        "Transaction amount " + billAmount + " is below the minimum required " + minOrder + " for this coupon");
            }
            discount = amt;
            if (discount.compareTo(billAmount) > 0) {
                discount = billAmount;
            }
        }

        BigDecimal finalAmount = billAmount.subtract(discount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        return CouponDiscountResult.builder()
                .customerCouponId(customerCoupon.getId())
                .originalAmount(billAmount)
                .discountApplied(discount)
                .finalAmount(finalAmount)
                .build();
    }
}
