package com.shabanaj.beloyal.features.couponLookup.dto;

import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class VariantLookupItem {
    private Long id;
    private String name;
    private CatalogStatus status;
    private BigDecimal price;
    private String currency;
}
