package com.shabanaj.beloyal.features.coupon.service.impl;

import com.shabanaj.beloyal.common.Exception.*;
import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.coupon.dto.CouponCreateRequest;
import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.coupon.service.CouponCreateService;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponCreateServiceImpl implements CouponCreateService {

    private final CouponRepository couponRepository;
    private final BusinessService businessService;
    private final CatalogCategoryRepository catalogCategoryRepository;
    private final CatalogItemRepository catalogItemRepository;
    private final CatalogItemVariantRepository catalogItemVariantRepository;
    private final CouponMapper couponMapper;

    @Override
    @Transactional
    public CouponDetailResponse create(Long businessId, CouponCreateRequest request) {
        Business business = businessService.getActiveBusinessById(businessId);

        validateDateRange(request.getStartDate(), request.getEndDate(), request.getStatus());

        LoyaltyCoupon coupon = LoyaltyCoupon.builder()
                .business(business)
                .type(request.getType())
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .pointsCost(request.getPointsCost())
                .currency(business.getCurrencyCode())
                .status(request.getStatus() != null ? request.getStatus() : CouponStatus.DRAFT)
                .visibility(request.getVisibility())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalRedemptionLimit(request.getTotalRedemptionLimit())
                .perCustomerRedemptionLimit(request.getPerCustomerRedemptionLimit())
                .termsAndConditions(request.getTermsAndConditions())
                .isFeatured(request.getIsFeatured() != null && request.getIsFeatured())
                .sortOrder(request.getSortOrder())
                .build();

        if (request.getType() == CouponType.FREE_PRODUCT) {
            applyFreeProductDetails(coupon, request, businessId);
        } else {
            applyDiscountDetails(coupon, request, business);
        }

        coupon = couponRepository.save(coupon);
        return couponMapper.toDetail(coupon);
    }

    private void applyFreeProductDetails(LoyaltyCoupon coupon, CouponCreateRequest request, Long businessId) {
        if (request.getCategoryId() == null) {
            throw new BadRequestException("categoryId is required for FREE_PRODUCT coupons");
        }
        if (request.getProductId() == null) {
            throw new BadRequestException("productId is required for FREE_PRODUCT coupons");
        }

        CatalogCategory category = catalogCategoryRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(request.getCategoryId(), businessId)
                .orElseThrow(() -> new CatalogCategoryNotFound("Category not found: " + request.getCategoryId()));

        if (category.getStatus() != CatalogStatus.ACTIVE) {
            throw new BadRequestException("Selected category is not active");
        }

        CatalogItem product = catalogItemRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(request.getProductId(), businessId)
                .orElseThrow(() -> new CatalogItemNotFound("Product not found: " + request.getProductId()));

        if (product.getStatus() != CatalogStatus.ACTIVE) {
            throw new BadRequestException("Selected product is not active");
        }

        if (!product.getCategory().getId().equals(category.getId())) {
            throw new BadRequestException("Selected product does not belong to the selected category");
        }

        CatalogItemVariant variant = null;
        if (request.getVariantId() != null) {
            variant = catalogItemVariantRepository
                    .findByIdAndCatalogItemIdAndIsDeletedFalse(request.getVariantId(), product.getId())
                    .orElseThrow(() -> new CatalogItemVariantNotFound(request.getVariantId()));

            if (variant.getStatus() != CatalogStatus.ACTIVE) {
                throw new BadRequestException("Selected variant is not active");
            }
        }

        // Image fallback
        if (coupon.getImageUrl() == null || coupon.getImageUrl().isBlank()) {
            coupon.setImageUrl(product.getImageUrl());
        }

        // Description fallback
        String description = request.getDescription();
        if (description == null || description.isBlank()) {
            if (variant != null) {
                description = "Redeem this coupon for one free " + variant.getName() + " " + product.getName()
                        + " using " + coupon.getPointsCost() + " loyalty points.";
            } else {
                description = "Redeem this coupon for one free " + product.getName()
                        + " using " + coupon.getPointsCost() + " loyalty points.";
            }
        }
        coupon.setDescription(description);

        CouponFreeProductDetails details = CouponFreeProductDetails.builder()
                .coupon(coupon)
                .category(category)
                .product(product)
                .variant(variant)
                .quantity(request.getQuantity() != null ? request.getQuantity() : 1)
                .build();

        coupon.setFreeProductDetails(details);
    }

    private void applyDiscountDetails(LoyaltyCoupon coupon, CouponCreateRequest request, Business business) {
        String description = request.getDescription();

        if (coupon.getType() == CouponType.PERCENTAGE_DISCOUNT) {
            if (request.getDiscountPercentage() == null) {
                throw new BadRequestException("discountPercentage is required for PERCENTAGE_DISCOUNT coupons");
            }
            if (description == null || description.isBlank()) {
                description = "Redeem this coupon for " + request.getDiscountPercentage().stripTrailingZeros().toPlainString()
                        + "% off your order using " + coupon.getPointsCost() + " loyalty points.";
            }
        } else if (coupon.getType() == CouponType.FIXED_AMOUNT_DISCOUNT) {
            if (request.getDiscountAmount() == null) {
                throw new BadRequestException("discountAmount is required for FIXED_AMOUNT_DISCOUNT coupons");
            }
            if (description == null || description.isBlank()) {
                description = "Redeem this coupon for " + request.getDiscountAmount().stripTrailingZeros().toPlainString()
                        + " " + business.getCurrencyCode().getSymbol()
                        + " off your order using " + coupon.getPointsCost() + " loyalty points.";
            }
        }

        coupon.setDescription(description);

        CouponDiscountDetails details = CouponDiscountDetails.builder()
                .coupon(coupon)
                .discountPercentage(request.getDiscountPercentage())
                .discountAmount(request.getDiscountAmount())
                .minimumOrderAmount(request.getMinimumOrderAmount())
                .maximumDiscountAmount(request.getMaximumDiscountAmount())
                .build();

        coupon.setDiscountDetails(details);
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate, CouponStatus status) {
        if (startDate != null && endDate != null && !startDate.isBefore(endDate)) {
            throw new BadRequestException("startDate must be before endDate");
        }
        if (status == CouponStatus.ACTIVE && endDate != null && endDate.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("endDate must be in the future when activating a coupon");
        }
    }
}
