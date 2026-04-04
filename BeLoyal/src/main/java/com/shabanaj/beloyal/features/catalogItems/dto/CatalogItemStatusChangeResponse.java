package com.shabanaj.beloyal.features.catalogItems.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CatalogItemStatusChangeResponse {
    private Long id;
    private String status;
    private Boolean isDeleted;
    private LocalDateTime updatedAt;
}
