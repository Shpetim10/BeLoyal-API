package com.shabanaj.beloyal.features.catalogCategories.service.impl;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.common.Exception.CatalogCategoryNotFound;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryOrderUpdateRequest;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryOrderUpdateRequest.CategoryOrderDto;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryViewDto;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryOrderManagementService;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles explicit reordering of catalog categories within a business.
 *
 * <h3>Supported modes</h3>
 * <ul>
 *   <li><b>Full reorder</b> – the request contains every category in the
 *       business.  The final order is exactly as specified.</li>
 *   <li><b>Partial reorder</b> – the request contains only a subset of
 *       categories.  The listed categories are placed first (in the requested
 *       order); all unlisted categories follow in their original relative
 *       order.</li>
 * </ul>
 *
 * <p>The unique constraint {@code uk_business_category_order_index} is
 * respected through a constraint-safe two-phase DB write (shift to large
 * negatives, flush, then assign final indices, flush again).
 */
@Service
@RequiredArgsConstructor
public class CatalogCategoryOrderManagementServiceImpl implements CatalogCategoryOrderManagementService {

    private final CatalogCategoryRepository catalogCategoryRepository;

    @Override
    @Transactional
    public List<CatalogCategoryViewDto> updateOrder(Long businessId, CatalogCategoryOrderUpdateRequest request) {

        // ── 1. Load all active categories for this business (current order) ───
        List<CatalogCategory> existing =
                catalogCategoryRepository.findAllByBusinessIdAndIsDeletedFalseOrderByOrderIndexAsc(businessId);

        if (existing.isEmpty()) {
            return Collections.emptyList();
        }

        // ── 2. Validate the incoming request ─────────────────────────────────
        List<CategoryOrderDto> dtos = request.getCategoryOrders();

        // 2a. No duplicate category IDs in the request
        Set<Long> seenCategoryIds = new HashSet<>();
        for (CategoryOrderDto dto : dtos) {
            if (!seenCategoryIds.add(dto.getCategoryId())) {
                throw new BadRequestException(
                        "Duplicate category id " + dto.getCategoryId() + " in the request");
            }
        }

        // 2b. All referenced category IDs must exist in this business
        Map<Long, CatalogCategory> categoryById = existing.stream()
                .collect(Collectors.toMap(CatalogCategory::getId, c -> c));

        for (CategoryOrderDto dto : dtos) {
            if (!categoryById.containsKey(dto.getCategoryId())) {
                throw new CatalogCategoryNotFound(
                        "Catalog Category with id " + dto.getCategoryId() + " not found for this business.");
            }
        }

        // 2c. No duplicate target indices in the request
        Set<Integer> seenIndices = new HashSet<>();
        for (CategoryOrderDto dto : dtos) {
            if (!seenIndices.add(dto.getOrderIndex())) {
                throw new BadRequestException(
                        "Duplicate order index " + dto.getOrderIndex() + " in the request");
            }
        }

        // 2d. All requested indices must be non-negative
        for (CategoryOrderDto dto : dtos) {
            if (dto.getOrderIndex() < 0) {
                throw new BadRequestException(
                        "Order index must be non-negative, got " + dto.getOrderIndex());
            }
        }

        // ── 3. Build the final ordered list ──────────────────────────────────
        //
        // Strategy (same as item reordering):
        //   • Sort the DTOs by their requested orderIndex (rank-based, not
        //     value-based) so only relative order matters.
        //   • Place explicitly requested categories first, in that rank order.
        //   • Append unlisted categories in their original stored order.

        List<CatalogCategory> requestedCategories = dtos.stream()
                .sorted(Comparator.comparingInt(CategoryOrderDto::getOrderIndex))
                .map(dto -> categoryById.get(dto.getCategoryId()))
                .collect(Collectors.toList());

        Set<Long> requestedIds = seenCategoryIds; // already populated
        List<CatalogCategory> unlistedCategories = existing.stream()
                .filter(c -> !requestedIds.contains(c.getId()))
                .collect(Collectors.toList()); // already in original order

        List<CatalogCategory> orderedCategories = new ArrayList<>(requestedCategories);
        orderedCategories.addAll(unlistedCategories);

        // ── 4. Persist using constraint-safe two-phase recompaction ───────────
        recompact(orderedCategories);

        // ── 5. Return only the explicitly requested categories ────────────────
        return requestedCategories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Assign consecutive 0-based order indices to the supplied list in list
     * order, using a two-phase write to avoid unique-constraint violations.
     *
     * <p>Phase 1: shift every row to a large negative sentinel that is
     * guaranteed unique within the set. Flush to DB.<br>
     * Phase 2: assign final 0..n-1 indices. Flush to DB.
     *
     * <p>A fast-path skips both flushes when the list is already compacted.
     */
    private void recompact(List<CatalogCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return;
        }

        // Fast-path: already correct order and compacted
        boolean alreadyCompact = true;
        for (int i = 0; i < categories.size(); i++) {
            Integer idx = categories.get(i).getOrderIndex();
            if (idx == null || idx != i) {
                alreadyCompact = false;
                break;
            }
        }
        if (alreadyCompact) {
            return;
        }

        // Phase 1: large negative sentinels (position-based, no int overflow)
        for (int i = 0; i < categories.size(); i++) {
            categories.get(i).setOrderIndex(-(1_000_000 + i));
        }
        catalogCategoryRepository.saveAllAndFlush(categories);

        // Phase 2: final 0-based indices
        for (int i = 0; i < categories.size(); i++) {
            categories.get(i).setOrderIndex(i);
        }
        catalogCategoryRepository.saveAllAndFlush(categories);
    }

    private CatalogCategoryViewDto toDto(CatalogCategory category) {
        return CatalogCategoryViewDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .orderIndex(category.getOrderIndex())
                .status(category.getStatus().getName())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
