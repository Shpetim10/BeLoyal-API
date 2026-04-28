package com.shabanaj.beloyal.features.catalogItemVariants.dto;

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
public class CatalogItemVariantOrderUpdateRequest {

    @NotEmpty(message = "Variant order list cannot be empty")
    @Valid
    private List<VariantOrderDto> variantOrders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantOrderDto {

        @NotNull(message = "Variant id is required")
        private Long variantId;

        @NotNull(message = "Order index is required")
        private Integer orderIndex;
    }
}
