package com.shabanaj.beloyal.features.catalogItems.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class CatalogItemDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String categoryName;
    private Long categoryId;
    private BigDecimal price;
    private String type;
    private String currencyCode;
    private String unit;
    private Integer orderIndex;
    private String imageUrl;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
