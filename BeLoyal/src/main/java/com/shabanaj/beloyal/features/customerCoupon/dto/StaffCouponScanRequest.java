package com.shabanaj.beloyal.features.customerCoupon.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffCouponScanRequest {

    @NotBlank
    private String qrCode;

    private String redemptionLocation;
}
