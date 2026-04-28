package com.shabanaj.beloyal.features.catalogItemVariants.service.impl;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantOrderUpdateRequest;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantOrderUpdateRequest.VariantOrderDto;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantSummaryResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantReorderService;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantService;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemService;
import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles explicit reordering of catalog item variants within a catalog item.
 *
 * <h3>Supported modes</h3>
 * <ul>
 *   <li><b>Full reorder</b> – every variant supplied; final order is exactly as specified.</li>
 *   <li><b>Partial reorder</b> – only a subset supplied; listed variants are placed first
 *       (in requested order) and unlisted variants follow in their current relative order.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class CatalogItemVariantReorderServiceImpl implements CatalogItemVariantReorderService {

    private final CatalogItemService catalogItemService;
    private final CatalogItemVariantService catalogItemVariantService;
    private final CatalogItemVariantRepository catalogItemVariantRepository;
    private final CatalogItemVariantOrderHelper orderHelper;

    @Override
    @Transactional
    public List<CatalogItemVariantSummaryResponse> reorder(Long businessId, Long catalogItemId,
                                                           CatalogItemVariantOrderUpdateRequest request) {

        // ── 1. Validate business owns the catalog item ───────────────────────
        catalogItemService.getByIdAndBusinessIdAndIsDeletedFalse(catalogItemId, businessId);

        // ── 2. Load all active variants in current order ─────────────────────
        List<CatalogItemVariant> existing =
                catalogItemVariantService.getCatalogItemVariantsByCatalogItemIdAndIsDeletedFalseOrderedByIndex(catalogItemId);

        if (existing.isEmpty()) {
            return Collections.emptyList();
        }

        // ── 3. Validate incoming request ──────────────────────────────────────
        List<VariantOrderDto> dtos = request.getVariantOrders();

        // 3a. No duplicate variant IDs
        Set<Long> seenVariantIds = new HashSet<>();
        for (VariantOrderDto dto : dtos) {
            if (!seenVariantIds.add(dto.getVariantId())) {
                throw new BadRequestException(
                        "Duplicate variant id " + dto.getVariantId() + " in the request");
            }
        }

        // 3b. All referenced variant IDs must exist in this catalog item
        Map<Long, CatalogItemVariant> variantById = existing.stream()
                .collect(Collectors.toMap(CatalogItemVariant::getId, v -> v));

        for (VariantOrderDto dto : dtos) {
            if (!variantById.containsKey(dto.getVariantId())) {
                throw new BadRequestException(
                        "Variant with id " + dto.getVariantId()
                                + " not found in catalog item " + catalogItemId);
            }
        }

        // 3c. No duplicate target indices
        Set<Integer> seenIndices = new HashSet<>();
        for (VariantOrderDto dto : dtos) {
            if (!seenIndices.add(dto.getOrderIndex())) {
                throw new BadRequestException(
                        "Duplicate order index " + dto.getOrderIndex() + " in the request");
            }
        }

        // 3d. All indices must be non-negative
        for (VariantOrderDto dto : dtos) {
            if (dto.getOrderIndex() < 0) {
                throw new BadRequestException(
                        "Order index must be non-negative, got " + dto.getOrderIndex());
            }
        }

        // ── 4. Build the final ordered list ──────────────────────────────────
        // Requested variants, sorted by the caller's intended rank
        List<CatalogItemVariant> requestedVariants = dtos.stream()
                .sorted(Comparator.comparingInt(VariantOrderDto::getOrderIndex))
                .map(dto -> variantById.get(dto.getVariantId()))
                .collect(Collectors.toList());

        // Unlisted variants in their current stored order
        List<CatalogItemVariant> unlistedVariants = existing.stream()
                .filter(v -> !seenVariantIds.contains(v.getId()))
                .collect(Collectors.toList());

        List<CatalogItemVariant> orderedVariants = new ArrayList<>(requestedVariants);
        orderedVariants.addAll(unlistedVariants);

        // ── 5. Persist using constraint-safe two-phase recompaction ───────────
        orderHelper.recompact(orderedVariants);

        // ── 6. Return the refreshed list in the new order ─────────────────────
        return catalogItemVariantRepository
                .findByCatalogItemIdAndIsDeletedFalseOrderByOrderIndexAsc(catalogItemId)
                .stream()
                .map(CatalogItemVariantViewServiceImpl::toSummaryResponse)
                .collect(Collectors.toList());
    }
}
