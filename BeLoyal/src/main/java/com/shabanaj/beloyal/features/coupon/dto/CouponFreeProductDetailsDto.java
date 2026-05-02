package com.shabanaj.beloyal.features.coupon.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponFreeProductDetailsDto {
    private Long categoryId;
    private String categoryName;
    private Long productId;
    private String productName;
    private Long variantId;
    private String variantName;
    private int quantity;
}
