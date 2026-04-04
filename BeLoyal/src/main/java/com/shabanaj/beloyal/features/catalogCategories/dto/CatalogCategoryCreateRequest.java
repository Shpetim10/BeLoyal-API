package com.shabanaj.beloyal.features.catalogCategories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CatalogCategoryCreateRequest {
    @Size(max = 120, message = "The name of category is too long")
    @NotNull
    @NotBlank
    private String name;

    @Size(max = 300, message = "The category description should not exceed 300 characters")
    private String description;
}
