package com.shabanaj.beloyal.features.couponLookup.dto;

import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductLookupItem {
    private Long id;
    private String name;
    private Long categoryId;
    private String imageUrl;
    private CatalogStatus status;
}
