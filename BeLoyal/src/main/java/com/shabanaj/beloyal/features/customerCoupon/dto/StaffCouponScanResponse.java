package com.shabanaj.beloyal.features.customerCoupon.dto;

import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StaffCouponScanResponse {
    private Long customerCouponId;
    private Long couponId;
    private CustomerCouponStatus status;
    private CouponType couponType;
    private String couponTitle;
    private Long snapshotProductId;
    private Long snapshotVariantId;
    private LocalDateTime usedAt;
    private String redemptionLocation;
}
