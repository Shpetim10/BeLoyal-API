package com.shabanaj.beloyal.features.catalogCategories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogCategoryShortResponse {
    private Long id;
    private String name;
}
