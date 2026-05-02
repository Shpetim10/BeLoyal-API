package com.shabanaj.beloyal.features.customerCoupon.service;

import com.shabanaj.beloyal.features.customerCoupon.dto.CouponRedeemResponse;
import com.shabanaj.beloyal.features.customerCoupon.dto.CustomerCouponDetailResponse;

import java.util.List;

public interface CouponRedemptionService {
    CouponRedeemResponse redeem(Long couponId, Long userId);
    List<CustomerCouponDetailResponse> getCustomerCoupons(Long userId);
    CustomerCouponDetailResponse applyCoupon(Long customerCouponId, Long userId, String orderId);
    CustomerCouponDetailResponse useCoupon(Long customerCouponId, Long userId);
}
