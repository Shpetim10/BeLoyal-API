package com.shabanaj.beloyal.features.catalogItemVariants.service.impl;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantDetailResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantUpdateRequest;
import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantService;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantUpdateService;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemService;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.shabanaj.beloyal.common.Configurations.JsonHelperConfig.*;

@Service
@RequiredArgsConstructor
public class CatalogItemVariantUpdateServiceImpl implements CatalogItemVariantUpdateService {

    private final CatalogItemService catalogItemService;
    private final CatalogItemVariantService catalogItemVariantService;
    private final CatalogItemVariantRepository catalogItemVariantRepository;

    @Override
    @Transactional
    public CatalogItemVariantDetailResponse update(Long businessId, Long catalogItemId, Long variantId,
                                                   CatalogItemVariantUpdateRequest request) {
        // 1. Verify business owns the catalog item
        CatalogItem catalogItem = catalogItemService.getByIdAndBusinessIdAndIsDeletedFalse(catalogItemId, businessId);

        // 2. Fetch the variant (must belong to this catalog item)
        CatalogItemVariant variant = catalogItemVariantService.getByIdAndCatalogItemId(variantId, catalogItemId);

        if (Boolean.TRUE.equals(variant.getIsDeleted())) {
            throw new BadRequestException("Cannot update a deleted catalog item variant");
        }

        // 3. Apply fields — uses JsonNullable so only explicitly sent fields are touched
        applyRequiredString(request.getName(), "name", variant::setName);
        applyOptionalString(request.getDescription(), variant::setDescription);
        applyOptionalDecimal(request, variant);

        // 4. Persist
        catalogItemVariantRepository.save(variant);

        // 5. Map to detail response
        return CatalogItemVariantViewServiceImpl.toDetailResponse(variant, catalogItem);
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private void applyOptionalDecimal(CatalogItemVariantUpdateRequest request, CatalogItemVariant variant) {
        if (request.getPriceOverride() == null || !request.getPriceOverride().isPresent()) {
            return; // not sent — no change
        }
        BigDecimal value = request.getPriceOverride().orElse(null);
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("priceOverride must be >= 0");
        }
        variant.setPriceOverride(value); // null = clear the override
    }
}
