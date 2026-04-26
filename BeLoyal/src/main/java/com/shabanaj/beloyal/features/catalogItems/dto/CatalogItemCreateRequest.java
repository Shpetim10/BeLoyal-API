package com.shabanaj.beloyal.features.catalogItems.dto;

import com.shabanaj.beloyal.model.Enums.CatalogItemType;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CatalogItemCreateRequest {
    @NotNull
    @NotBlank
    @Size(max=120)
    private String name;

    @Size(max=500)
    private String description;

    @NotNull
    @DecimalMin(value = "0.1", inclusive = true)
    private BigDecimal price;

    @NotNull
    private CatalogItemType type;

    @NotNull
    private CurrencyCode  currency;

    private String unit;
    private String imageUrl;
    private String imageKey;
}
