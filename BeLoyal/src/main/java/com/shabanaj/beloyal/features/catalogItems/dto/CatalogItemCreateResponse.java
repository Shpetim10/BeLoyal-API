package com.shabanaj.beloyal.features.catalogItems.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CatalogItemCreateResponse {
    private String name;
    private String description;
    private BigDecimal price;
    private String currencyCode;
    private String type;
    private String unit;
    private String status;
    private String imageUrl;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
