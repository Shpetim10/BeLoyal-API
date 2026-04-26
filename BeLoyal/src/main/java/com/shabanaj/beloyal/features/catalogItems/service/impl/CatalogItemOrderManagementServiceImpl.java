package com.shabanaj.beloyal.features.catalogItems.service.impl;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemOrderUpdateRequest;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemOrderUpdateRequest.ItemOrderDto;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemShortResponse;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemOrderManagementService;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles explicit reordering of catalog items within a category.
 *
 * <h3>Supported modes</h3>
 * <ul>
 *   <li><b>Full reorder</b> – the request contains every item in the category.
 *       The final order is exactly as specified.</li>
 *   <li><b>Partial reorder</b> – the request contains only a subset of items.
 *       The listed items are placed first (in the requested order); all
 *       unlisted items follow in their original relative order.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class CatalogItemOrderManagementServiceImpl implements CatalogItemOrderManagementService {

    private final CatalogItemRepository catalogItemRepository;
    private final CatalogCategoryRepository catalogCategoryRepository;
    private final CatalogItemOrderHelper orderHelper;

    @Override
    @Transactional
    public List<CatalogItemShortResponse> updateOrder(Long businessId, Long categoryId,
                                                      CatalogItemOrderUpdateRequest request) {

        // ── 1. Resolve and validate the category ─────────────────────────────
        CatalogCategory category = catalogCategoryRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(categoryId, businessId)
                .orElseThrow(() -> new BadRequestException("Category not found for this business"));

        // ── 2. Load all active items in this category (current order) ─────────
        List<CatalogItem> existing =
                catalogItemRepository.findAllByCategoryAndIsDeletedFalseOrderByOrderIndexAsc(category);

        if (existing.isEmpty()) {
            return Collections.emptyList();
        }

        // ── 3. Validate the incoming request ─────────────────────────────────
        List<ItemOrderDto> dtos = request.getItemOrders();

        // 3a. No duplicate item IDs in the request
        Set<Long> seenItemIds = new HashSet<>();
        for (ItemOrderDto dto : dtos) {
            if (!seenItemIds.add(dto.getItemId())) {
                throw new BadRequestException(
                        "Duplicate item id " + dto.getItemId() + " in the request");
            }
        }

        // 3b. All referenced item IDs must actually exist in this category
        Map<Long, CatalogItem> itemById = existing.stream()
                .collect(Collectors.toMap(CatalogItem::getId, i -> i));

        for (ItemOrderDto dto : dtos) {
            if (!itemById.containsKey(dto.getItemId())) {
                throw new BadRequestException(
                        "Item with id " + dto.getItemId() + " not found in category " + categoryId);
            }
        }

        // 3c. No duplicate target indices in the request
        Set<Integer> seenIndices = new HashSet<>();
        for (ItemOrderDto dto : dtos) {
            if (!seenIndices.add(dto.getOrderIndex())) {
                throw new BadRequestException(
                        "Duplicate order index " + dto.getOrderIndex() + " in the request");
            }
        }

        // 3d. All requested indices must be non-negative
        for (ItemOrderDto dto : dtos) {
            if (dto.getOrderIndex() < 0) {
                throw new BadRequestException(
                        "Order index must be non-negative, got " + dto.getOrderIndex());
            }
        }

        // ── 4. Build the final ordered list ──────────────────────────────────
        //
        // Strategy:
        //   • Sort the DTOs by their requested orderIndex so we know the
        //     caller's intended relative order (ignoring the actual numeric
        //     values – only rank matters).
        //   • Place explicitly requested items first, in that rank order.
        //   • Append the remaining (unrequested) items in their original order.
        //
        // This guarantees a well-defined, gap-free result regardless of
        // whether the request is partial or full.

        // Items explicitly listed in the request, sorted by requested index
        List<CatalogItem> requestedItems = dtos.stream()
                .sorted(Comparator.comparingInt(ItemOrderDto::getOrderIndex))
                .map(dto -> itemById.get(dto.getItemId()))
                .collect(Collectors.toList());

        // Items NOT listed in the request, in their current stored order
        Set<Long> requestedIds = seenItemIds; // already populated
        List<CatalogItem> unlistedItems = existing.stream()
                .filter(item -> !requestedIds.contains(item.getId()))
                .collect(Collectors.toList()); // already sorted by OrderIndexAsc

        // Final order: requested first, then unlisted
        List<CatalogItem> orderedItems = new ArrayList<>(requestedItems);
        orderedItems.addAll(unlistedItems);

        // ── 5. Persist using the constraint-safe two-phase recompaction ───────
        orderHelper.recompact(orderedItems);

        // ── 6. Return the refreshed list in new order ─────────────────────────
        return catalogItemRepository
                .findAllByCategoryAndIsDeletedFalseOrderByOrderIndexAsc(category)
                .stream()
                .map(item -> CatalogItemShortResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .status(item.getStatus().getName())
                        .categoryName(category.getName())
                        .price(item.getPrice())
                        .orderIndex(item.getOrderIndex())
                        .imageUrl(item.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
