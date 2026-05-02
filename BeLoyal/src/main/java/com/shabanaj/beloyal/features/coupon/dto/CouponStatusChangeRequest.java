package com.shabanaj.beloyal.features.coupon.dto;

import com.shabanaj.beloyal.model.Enums.CouponStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponStatusChangeRequest {
    @NotNull(message = "Status is required")
    private CouponStatus status;
}
