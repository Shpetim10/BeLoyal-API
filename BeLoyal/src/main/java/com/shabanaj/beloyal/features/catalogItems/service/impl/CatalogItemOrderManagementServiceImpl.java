package com.shabanaj.beloyal.features.catalogItems.service.impl;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemOrderUpdateRequest;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemShortResponse;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemOrderManagementService;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        // 1. Resolve and validate the category
        CatalogCategory category = catalogCategoryRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(categoryId, businessId)
                .orElseThrow(() -> new BadRequestException("Category not found for this business"));

        // 2. Load all non-deleted items in this category
        List<CatalogItem> existing =
                catalogItemRepository.findAllByCategoryAndIsDeletedFalseOrderByOrderIndexAsc(category);

        int totalItems = existing.size();

        // 3. Build a lookup map: itemId → item
        Map<Long, CatalogItem> itemMap = existing.stream()
                .collect(Collectors.toMap(CatalogItem::getId, i -> i));

        // 4. Validate request entries
        Set<Integer> seenIndices = new HashSet<>();
        for (CatalogItemOrderUpdateRequest.ItemOrderDto dto : request.getItemOrders()) {
            // All referenced IDs must belong to this category
            if (!itemMap.containsKey(dto.getItemId())) {
                throw new BadRequestException(
                        "Item with id " + dto.getItemId() + " not found in this category");
            }
            // Index must be in valid range [0, totalItems - 1]
            if (dto.getOrderIndex() < 0 || dto.getOrderIndex() >= totalItems) {
                throw new BadRequestException(
                        "Order index " + dto.getOrderIndex() + " is out of range [0, " + (totalItems - 1) + "]");
            }
            // No duplicate indices in the request
            if (!seenIndices.add(dto.getOrderIndex())) {
                throw new BadRequestException(
                        "Duplicate order index " + dto.getOrderIndex() + " in the request");
            }
        }

        // 5. Apply the requested indices
        for (CatalogItemOrderUpdateRequest.ItemOrderDto dto : request.getItemOrders()) {
            CatalogItem item = itemMap.get(dto.getItemId());
            item.setOrderIndex(dto.getOrderIndex());
            catalogItemRepository.save(item);
        }

        // 6. Normalise / recompact the whole category so any un-referenced items
        //    (partial reorder) are also left gap-free.
        orderHelper.recompact(category);

        // 7. Return the refreshed list in order
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
