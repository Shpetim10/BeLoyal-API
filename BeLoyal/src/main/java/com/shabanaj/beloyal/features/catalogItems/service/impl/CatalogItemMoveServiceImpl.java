package com.shabanaj.beloyal.features.catalogItems.service.impl;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemDetailResponse;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemMoveService;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogItemMoveServiceImpl implements CatalogItemMoveService {

    private final CatalogItemRepository catalogItemRepository;
    private final CatalogCategoryRepository catalogCategoryRepository;
    private final CatalogItemOrderHelper orderHelper;

    @Override
    @Transactional
    public CatalogItemDetailResponse move(Long businessId, Long itemId, Long newCategoryId) {
        // 1. Fetch the item
        CatalogItem item = catalogItemRepository.findByIdAndBusinessIdAndIsDeletedFalse(itemId, businessId)
                .orElseThrow(() -> new BadRequestException("Catalog item not found for this business"));

        CatalogCategory oldCategory = item.getCategory();

        // 2. Validate that we are actually moving to a different category
        if (oldCategory.getId().equals(newCategoryId)) {
            throw new BadRequestException("Item is already in this category");
        }

        // 3. Fetch the new category — it must belong to the same business and not be deleted
        CatalogCategory newCategory = catalogCategoryRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(newCategoryId, businessId)
                .orElseThrow(() -> new BadRequestException("Target category not found for this business"));

        // 4. Assign item to new category with the next available order-index
        Integer nextIndex = catalogItemRepository.countByCategoryIdAndIsDeletedFalse(newCategoryId);
        item.setCategory(newCategory);
        item.setOrderIndex(nextIndex);
        catalogItemRepository.save(item);

        // 5. Recompact order-indices in the old category (fill the gap left behind)
        orderHelper.recompact(oldCategory);

        return CatalogItemUpdateServiceImpl.toDetailResponse(item);
    }
}
