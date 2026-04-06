package com.shabanaj.beloyal.features.catalogItems.service.impl;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.common.image_upload.ImageUploadService;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemDetailResponse;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemUpdateRequest;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemUpdateService;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.shabanaj.beloyal.common.Configurations.JsonHelperConfig.*;

@Service
@RequiredArgsConstructor
public class CatalogItemUpdateServiceImpl implements CatalogItemUpdateService {

    private final CatalogItemRepository catalogItemRepository;
    private final ImageUploadService imageUploadService;

    @Override
    @Transactional
    public CatalogItemDetailResponse update(Long businessId, Long itemId, CatalogItemUpdateRequest request) {
        // 1. Fetch non-deleted item belonging to this business
        CatalogItem item = catalogItemRepository.findByIdAndBusinessIdAndIsDeletedFalse(itemId, businessId)
                .orElseThrow(() -> new BadRequestException("Catalog item not found for this business"));

        // 2. Apply mandatory fields (if sent, cannot be null/blank)
        applyRequiredString(request.getName(), "name", item::setName);
        applyRequiredDecimal(request.getPrice(), "price", BigDecimal.valueOf(0.1), item::setPrice);
        applyRequiredEnum(request.getType(), "type", item::setType);
        applyRequiredEnum(request.getCurrency(), "currencyCode", item::setCurrencyCode);

        // 3. Apply optional fields (null = clear)
        applyOptionalString(request.getDescription(), item::setDescription);
        applyOptionalString(request.getUnit(), item::setUnit);

        // 4. Handle image pair
        handleImageUpdate(request, item);

        // 5. Persist
        catalogItemRepository.save(item);

        // 6. Return detail response
        return toDetailResponse(item);
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private void handleImageUpdate(CatalogItemUpdateRequest request, CatalogItem item) {
        boolean urlSent = isPresent(request.getImageUrl());
        boolean keySent = isPresent(request.getImageKey());

        if (!urlSent && !keySent) return; // neither sent → no change

        // Both must be sent together
        if (urlSent != keySent) {
            throw new BadRequestException("imageUrl and imageKey must be provided together");
        }

        String newUrl = normalizeOptional(request.getImageUrl());
        String newKey = normalizeOptional(request.getImageKey());

        // Both must be null or both must have values
        if ((newUrl == null) != (newKey == null)) {
            throw new BadRequestException("imageUrl and imageKey must both be set or both be cleared together");
        }

        // Delete old image from storage if one existed
        String oldKey = item.getImageKey();
        if (oldKey != null && !oldKey.isBlank()) {
            imageUploadService.deleteByKey(oldKey);
        }

        item.setImageUrl(newUrl);
        item.setImageKey(newKey);
    }

    static CatalogItemDetailResponse toDetailResponse(CatalogItem item) {
        return CatalogItemDetailResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .status(item.getStatus().name())
                .categoryName(item.getCategory().getName())
                .categoryId(item.getCategory().getId())
                .price(item.getPrice())
                .type(item.getType().getName())
                .currencyCode(item.getCurrencyCode().name())
                .unit(item.getUnit())
                .orderIndex(item.getOrderIndex())
                .imageUrl(item.getImageUrl())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
