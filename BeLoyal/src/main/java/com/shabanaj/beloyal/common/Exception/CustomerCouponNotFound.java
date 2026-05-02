package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CustomerCouponNotFound extends ApiException {
    public CustomerCouponNotFound(Long customerCouponId) {
        super(HttpStatus.NOT_FOUND, "Customer coupon not found: " + customerCouponId);
    }
}
