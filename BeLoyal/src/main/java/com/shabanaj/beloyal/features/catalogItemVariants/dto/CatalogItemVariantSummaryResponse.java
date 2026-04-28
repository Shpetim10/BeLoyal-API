package com.shabanaj.beloyal.features.catalogItemVariants.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CatalogItemVariantSummaryResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal priceOverride;
    private String status;
    private Integer orderIndex;
}
