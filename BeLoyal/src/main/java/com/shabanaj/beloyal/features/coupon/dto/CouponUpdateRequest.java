package com.shabanaj.beloyal.features.coupon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shabanaj.beloyal.model.Enums.CouponVisibility;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CouponUpdateRequest {

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @Min(value = 1, message = "Points cost must be greater than 0")
    private Integer pointsCost;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Min(value = 1, message = "Total redemption limit must be positive")
    private Integer totalRedemptionLimit;

    @Min(value = 1, message = "Per customer redemption limit must be positive")
    private Integer perCustomerRedemptionLimit;

    private CouponVisibility visibility;

    @Size(max = 2000, message = "Terms and conditions must not exceed 2000 characters")
    private String termsAndConditions;

    private Boolean isFeatured;
    private Integer sortOrder;

    // FREE_PRODUCT updatable fields
    private Long variantId;
    private Integer quantity;

    // Discount updatable fields
    @DecimalMin(value = "0.01", message = "Discount percentage must be greater than 0")
    @DecimalMax(value = "100", message = "Discount percentage must not exceed 100")
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.01", message = "Discount amount must be greater than 0")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "Minimum order amount must be non-negative")
    private BigDecimal minimumOrderAmount;

    @DecimalMin(value = "0.01", message = "Maximum discount amount must be greater than 0")
    private BigDecimal maximumDiscountAmount;
}
