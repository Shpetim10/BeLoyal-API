package com.shabanaj.beloyal.features.catalogCategories.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogCategoryOrderUpdateRequest {

    @NotEmpty(message = "Categories order list cannot be empty")
    @Valid
    private List<CategoryOrderDto> categoryOrders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryOrderDto {
        @NotNull(message = "Category id is required")
        private Long categoryId;

        @NotNull(message = "Order index is required")
        private Integer orderIndex;
    }
}
