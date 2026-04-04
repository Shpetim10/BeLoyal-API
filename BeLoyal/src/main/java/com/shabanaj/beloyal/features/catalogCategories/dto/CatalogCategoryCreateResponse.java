package com.shabanaj.beloyal.features.catalogCategories.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CatalogCategoryCreateResponse {
    private Long id;
    private String name;
    private String description;
    private Integer orderIndex;
    private String status;
}
