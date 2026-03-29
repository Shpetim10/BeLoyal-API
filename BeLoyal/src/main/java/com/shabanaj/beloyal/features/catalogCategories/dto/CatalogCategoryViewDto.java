package com.shabanaj.beloyal.features.catalogCategories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CatalogCategoryViewDto {
    private Long id;
    private String name;
    private String description;
    private Integer orderIndex;
    private String status;
    private LocalDateTime createdAt;
}
