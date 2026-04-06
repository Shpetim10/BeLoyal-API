package com.shabanaj.beloyal.features.catalogItems.dto;

import com.shabanaj.beloyal.model.Enums.CatalogItemType;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;

@Getter
@Setter
public class CatalogItemUpdateRequest {
    // Mandatory fields — if sent, cannot be null/blank
    private JsonNullable<String> name            = JsonNullable.undefined();
    private JsonNullable<BigDecimal> price        = JsonNullable.undefined();
    private JsonNullable<CatalogItemType> type    = JsonNullable.undefined();
    private JsonNullable<CurrencyCode> currency   = JsonNullable.undefined();

    // Optional fields — can be sent as null to clear
    private JsonNullable<String> description      = JsonNullable.undefined();
    private JsonNullable<String> unit             = JsonNullable.undefined();

    // Image pair — must be sent together; both null = clear image
    private JsonNullable<String> imageUrl         = JsonNullable.undefined();
    private JsonNullable<String> imageKey         = JsonNullable.undefined();
}
