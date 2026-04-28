package com.shabanaj.beloyal.features.catalogItemVariants.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CatalogItemVariantStatusChangeResponse {
    private Long id;
    private String status;
    private Boolean isDeleted;
    private LocalDateTime updatedAt;
}
