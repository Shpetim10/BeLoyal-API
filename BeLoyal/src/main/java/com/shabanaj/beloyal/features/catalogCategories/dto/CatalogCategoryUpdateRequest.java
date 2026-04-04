package com.shabanaj.beloyal.features.catalogCategories.dto;

import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class CatalogCategoryUpdateRequest {
    private JsonNullable<String> name = JsonNullable.undefined();
    private JsonNullable<String> description = JsonNullable.undefined();
    private JsonNullable<Integer> orderIndex = JsonNullable.undefined();
    private JsonNullable<CatalogStatus> status = JsonNullable.undefined();
}
