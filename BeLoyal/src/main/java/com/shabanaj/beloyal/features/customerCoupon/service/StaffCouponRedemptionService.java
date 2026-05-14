package com.shabanaj.beloyal.features.customerCoupon.service;

import com.shabanaj.beloyal.features.customerCoupon.dto.StaffCouponScanRequest;
import com.shabanaj.beloyal.features.customerCoupon.dto.StaffCouponScanResponse;

public interface StaffCouponRedemptionService {
    StaffCouponScanResponse scanAndRedeem(Long businessId, Long staffUserId, StaffCouponScanRequest request);
}
