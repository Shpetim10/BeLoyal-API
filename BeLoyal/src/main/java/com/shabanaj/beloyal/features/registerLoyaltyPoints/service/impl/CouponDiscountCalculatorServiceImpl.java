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

        if (now.isBefore(coupon.getStartDate())) {
            throw new CouponNotYetValidException();
        }

        if (now.isAfter(coupon.getEndDate())) {
            throw new CouponExpiredException();
        }

        CouponDiscountDetails details = coupon.getDiscountDetails();
        BigDecimal discount;

        if (coupon.getType() == CouponType.PERCENTAGE_DISCOUNT) {
            if (details == null || details.getDiscountPercentage() == null) {
                throw new InvalidCouponOperationException("Coupon discount details are missing");
            }
            discount = billAmount.multiply(details.getDiscountPercentage())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            if (details.getMaximumDiscountAmount() != null && discount.compareTo(details.getMaximumDiscountAmount()) > 0) {
                discount = details.getMaximumDiscountAmount();
            }
        } else {
            // FIXED_AMOUNT_DISCOUNT
            if (details == null || details.getDiscountAmount() == null) {
                throw new InvalidCouponOperationException("Coupon discount details are missing");
            }
            if (details.getMinimumOrderAmount() != null && billAmount.compareTo(details.getMinimumOrderAmount()) < 0) {
                throw new InvalidCouponOperationException(
                        "Transaction amount " + billAmount + " is below the minimum required " + details.getMinimumOrderAmount() + " for this coupon");
            }
            discount = details.getDiscountAmount();
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
