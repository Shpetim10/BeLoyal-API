package com.shabanaj.beloyal.features.catalogItemVariants.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class CatalogItemVariantDetailResponse {
    private Long id;
    private Long catalogItemId;
    private String name;
    private String description;
    private BigDecimal priceOverride;
    private String status;
    private Integer orderIndex;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
