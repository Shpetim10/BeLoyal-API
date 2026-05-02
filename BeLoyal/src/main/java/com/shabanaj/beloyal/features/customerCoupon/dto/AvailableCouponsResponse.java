package com.shabanaj.beloyal.features.customerCoupon.dto;

import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AvailableCouponsResponse {
    private int customerPointBalance;
    private CurrencyCode businessCurrency;
    private List<AvailableCouponItem> coupons;
}
