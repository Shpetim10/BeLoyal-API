package com.shabanaj.beloyal.features.catalogItems.service.impl;

import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Shared utility that re-assigns consecutive 0-based order-indices to all
 * non-deleted items in a category. Called after delete, restore, and move to
 * keep indices contiguous and gap-free.
 */
@Component
@RequiredArgsConstructor
public class CatalogItemOrderHelper {

    private final CatalogItemRepository catalogItemRepository;

    /**
     * Recompact all non-deleted items for this category by their current order.
     * Only items whose index actually needs to change are persisted.
     */
    public void recompact(CatalogCategory category) {
        List<CatalogItem> items =
                catalogItemRepository.findAllByCategoryAndIsDeletedFalseOrderByOrderIndexAsc(category);

        for (int i = 0; i < items.size(); i++) {
            CatalogItem item = items.get(i);
            if (item.getOrderIndex() != i) {
                item.setOrderIndex(i);
                catalogItemRepository.save(item);
            }
        }
    }
}
