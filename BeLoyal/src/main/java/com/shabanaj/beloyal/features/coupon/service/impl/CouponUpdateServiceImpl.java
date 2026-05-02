package com.shabanaj.beloyal.features.coupon.service.impl;

import com.shabanaj.beloyal.common.Exception.*;
import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.features.coupon.dto.CouponUpdateRequest;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.coupon.service.CouponUpdateService;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;
import com.shabanaj.beloyal.model.Entity.CouponDiscountDetails;
import com.shabanaj.beloyal.model.Entity.CouponFreeProductDetails;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponUpdateServiceImpl implements CouponUpdateService {

    private final CouponRepository couponRepository;
    private final CatalogItemVariantRepository catalogItemVariantRepository;
    private final CustomerCouponRepository customerCouponRepository;
    private final CouponMapper couponMapper;

    @Override
    @Transactional
    public CouponDetailResponse update(Long businessId, Long couponId, CouponUpdateRequest request) {
        LoyaltyCoupon coupon = couponRepository
                .findByIdAndBusinessIdAndDeletedAtIsNull(couponId, businessId)
                .orElseThrow(() -> new CouponNotFound(couponId));

        boolean hasRedemptions = customerCouponRepository.existsByCouponId(couponId);

        if (request.getTitle() != null) coupon.setTitle(request.getTitle());
        if (request.getDescription() != null) coupon.setDescription(request.getDescription());
        if (request.getImageUrl() != null) coupon.setImageUrl(request.getImageUrl());
        if (request.getVisibility() != null) coupon.setVisibility(request.getVisibility());
        if (request.getTermsAndConditions() != null) coupon.setTermsAndConditions(request.getTermsAndConditions());
        if (request.getIsFeatured() != null) coupon.setFeatured(request.getIsFeatured());
        if (request.getSortOrder() != null) coupon.setSortOrder(request.getSortOrder());

        if (request.getPointsCost() != null) {
            if (hasRedemptions) {
                throw new InvalidCouponOperationException("Cannot change points cost of a coupon that has been redeemed");
            }
            coupon.setPointsCost(request.getPointsCost());
        }

        if (request.getStartDate() != null) coupon.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) coupon.setEndDate(request.getEndDate());

        if (coupon.getStartDate() != null && coupon.getEndDate() != null
                && !coupon.getStartDate().isBefore(coupon.getEndDate())) {
            throw new BadRequestException("startDate must be before endDate");
        }

        if (request.getTotalRedemptionLimit() != null) {
            if (hasRedemptions && request.getTotalRedemptionLimit() < coupon.getTotalRedemptions()) {
                throw new InvalidCouponOperationException(
                        "Cannot set total redemption limit below current redemptions count (" + coupon.getTotalRedemptions() + ")");
            }
            coupon.setTotalRedemptionLimit(request.getTotalRedemptionLimit());
        }

        if (request.getPerCustomerRedemptionLimit() != null) {
            coupon.setPerCustomerRedemptionLimit(request.getPerCustomerRedemptionLimit());
        }

        if (coupon.getType() == CouponType.FREE_PRODUCT) {
            updateFreeProductDetails(coupon, request, hasRedemptions);
        } else {
            updateDiscountDetails(coupon, request);
        }

        coupon = couponRepository.save(coupon);
        return couponMapper.toDetail(coupon);
    }

    private void updateFreeProductDetails(LoyaltyCoupon coupon, CouponUpdateRequest request, boolean hasRedemptions) {
        CouponFreeProductDetails details = coupon.getFreeProductDetails();
        if (details == null) return;

        if (request.getVariantId() != null && hasRedemptions) {
            throw new InvalidCouponOperationException("Cannot change variant of a coupon that has been redeemed");
        }

        if (request.getVariantId() != null) {
            CatalogItemVariant variant = catalogItemVariantRepository
                    .findByIdAndCatalogItemId(request.getVariantId(), details.getProduct().getId())
                    .orElseThrow(() -> new CatalogItemVariantNotFound(request.getVariantId()));

            if (variant.getStatus() != CatalogStatus.ACTIVE) {
                throw new BadRequestException("Selected variant is not active");
            }
            details.setVariant(variant);
        }

        if (request.getQuantity() != null) {
            details.setQuantity(request.getQuantity());
        }
    }

    private void updateDiscountDetails(LoyaltyCoupon coupon, CouponUpdateRequest request) {
        CouponDiscountDetails details = coupon.getDiscountDetails();
        if (details == null) return;

        if (request.getDiscountPercentage() != null) details.setDiscountPercentage(request.getDiscountPercentage());
        if (request.getDiscountAmount() != null) details.setDiscountAmount(request.getDiscountAmount());
        if (request.getMinimumOrderAmount() != null) details.setMinimumOrderAmount(request.getMinimumOrderAmount());
        if (request.getMaximumDiscountAmount() != null) details.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
    }
}
