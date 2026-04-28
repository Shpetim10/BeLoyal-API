package com.shabanaj.beloyal.features.catalogItemVariants.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CatalogItemVariantCreateRequest {
    @NotNull
    private Long catalogItemId;

    @NotNull
    @NotBlank
    private String name;

    @Size(max=500, message = "Description should be no longer than 500 characters")
    private String description;

    @DecimalMin(value="0.0")
    private BigDecimal priceOverride;
}
