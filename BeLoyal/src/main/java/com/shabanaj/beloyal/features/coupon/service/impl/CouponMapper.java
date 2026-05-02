package com.shabanaj.beloyal.features.coupon.service.impl;

import com.shabanaj.beloyal.features.coupon.dto.*;
import com.shabanaj.beloyal.model.Entity.CouponDiscountDetails;
import com.shabanaj.beloyal.model.Entity.CouponFreeProductDetails;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public CouponDetailResponse toDetail(LoyaltyCoupon coupon) {
        return CouponDetailResponse.builder()
                .id(coupon.getId())
                .businessId(coupon.getBusiness().getId())
                .type(coupon.getType())
                .title(coupon.getTitle())
                .description(coupon.getDescription())
                .imageUrl(coupon.getImageUrl())
                .pointsCost(coupon.getPointsCost())
                .currency(coupon.getCurrency())
                .status(coupon.getStatus())
                .visibility(coupon.getVisibility())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .totalRedemptionLimit(coupon.getTotalRedemptionLimit())
                .totalRedemptions(coupon.getTotalRedemptions())
                .perCustomerRedemptionLimit(coupon.getPerCustomerRedemptionLimit())
                .termsAndConditions(coupon.getTermsAndConditions())
                .isFeatured(coupon.isFeatured())
                .sortOrder(coupon.getSortOrder())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .freeProductDetails(mapFreeProductDetails(coupon.getFreeProductDetails()))
                .discountDetails(mapDiscountDetails(coupon.getDiscountDetails()))
                .build();
    }

    public CouponSummaryResponse toSummary(LoyaltyCoupon coupon) {
        return CouponSummaryResponse.builder()
                .id(coupon.getId())
                .type(coupon.getType())
                .title(coupon.getTitle())
                .imageUrl(coupon.getImageUrl())
                .pointsCost(coupon.getPointsCost())
                .currency(coupon.getCurrency())
                .status(coupon.getStatus())
                .visibility(coupon.getVisibility())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .totalRedemptionLimit(coupon.getTotalRedemptionLimit())
                .totalRedemptions(coupon.getTotalRedemptions())
                .isFeatured(coupon.isFeatured())
                .createdAt(coupon.getCreatedAt())
                .build();
    }

    private CouponFreeProductDetailsDto mapFreeProductDetails(CouponFreeProductDetails d) {
        if (d == null) return null;
        return CouponFreeProductDetailsDto.builder()
                .categoryId(d.getCategory().getId())
                .categoryName(d.getCategory().getName())
                .productId(d.getProduct().getId())
                .productName(d.getProduct().getName())
                .variantId(d.getVariant() != null ? d.getVariant().getId() : null)
                .variantName(d.getVariant() != null ? d.getVariant().getName() : null)
                .quantity(d.getQuantity())
                .build();
    }

    private CouponDiscountDetailsDto mapDiscountDetails(CouponDiscountDetails d) {
        if (d == null) return null;
        return CouponDiscountDetailsDto.builder()
                .discountPercentage(d.getDiscountPercentage())
                .discountAmount(d.getDiscountAmount())
                .minimumOrderAmount(d.getMinimumOrderAmount())
                .maximumDiscountAmount(d.getMaximumDiscountAmount())
                .build();
    }
}
