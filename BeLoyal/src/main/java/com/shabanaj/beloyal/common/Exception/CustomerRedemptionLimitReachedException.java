package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CustomerRedemptionLimitReachedException extends ApiException {
    public CustomerRedemptionLimitReachedException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "You have reached the maximum redemption limit for this coupon");
    }
}
