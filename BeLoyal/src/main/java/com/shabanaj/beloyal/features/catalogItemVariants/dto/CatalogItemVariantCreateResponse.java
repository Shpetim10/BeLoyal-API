package com.shabanaj.beloyal.features.catalogItemVariants.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CatalogItemVariantCreateResponse {
    private String name;
    private String description;
    private BigDecimal price;
    private String status;
    private Integer orderIndex;
}
