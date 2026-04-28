package com.shabanaj.beloyal.features.catalogItemVariants.dto;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;

/**
 * PATCH request body for updating a catalog item variant.
 *
 * <p>Only fields that are explicitly present (not {@code JsonNullable.undefined()})
 * will be applied. Sending a field as JSON {@code null} clears its value
 * (where the field is optional).
 */
@Getter
@Setter
public class CatalogItemVariantUpdateRequest {

    /** Required if sent — cannot be null or blank. */
    private JsonNullable<String> name        = JsonNullable.undefined();

    /** Optional — send as null to clear the description. */
    private JsonNullable<String> description = JsonNullable.undefined();

    /** Optional — send as null to remove the price override. */
    private JsonNullable<BigDecimal> priceOverride = JsonNullable.undefined();
}
