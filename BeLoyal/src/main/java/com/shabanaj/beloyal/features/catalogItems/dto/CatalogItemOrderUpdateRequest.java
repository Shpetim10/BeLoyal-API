package com.shabanaj.beloyal.features.catalogItems.dto;

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
public class CatalogItemOrderUpdateRequest {

    @NotEmpty(message = "Item order list cannot be empty")
    @Valid
    private List<ItemOrderDto> itemOrders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemOrderDto {
        @NotNull(message = "Item id is required")
        private Long itemId;

        @NotNull(message = "Order index is required")
        private Integer orderIndex;
    }
}
