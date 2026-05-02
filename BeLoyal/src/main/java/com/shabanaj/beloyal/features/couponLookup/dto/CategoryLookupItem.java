package com.shabanaj.beloyal.features.couponLookup.dto;

import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryLookupItem {
    private Long id;
    private String name;
    private CatalogStatus status;
}
