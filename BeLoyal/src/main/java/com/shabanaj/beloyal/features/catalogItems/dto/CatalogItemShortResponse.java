package com.shabanaj.beloyal.features.catalogItems.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CatalogItemShortResponse {
    private Long id;
    private String name;
    private String status;
    private String categoryName;
    private BigDecimal price;
    private Integer orderIndex;
    private String imageUrl;
}
