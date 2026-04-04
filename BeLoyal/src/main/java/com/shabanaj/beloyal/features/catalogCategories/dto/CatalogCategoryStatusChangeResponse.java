package com.shabanaj.beloyal.features.catalogCategories.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CatalogCategoryStatusChangeResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private Integer orderIndex;
    private LocalDateTime createdAt;
}
