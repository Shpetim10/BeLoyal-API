package com.shabanaj.beloyal.features.catalogCategories.service.impl;

import com.shabanaj.beloyal.common.Exception.CatalogCategoryNotFound;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryService;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogCategoryServiceImpl implements CatalogCategoryService {
    private final CatalogCategoryRepository catalogCategoryRepository;
    private final CatalogItemRepository catalogItemRepository;


    public void save(CatalogCategory catalogCategory) {
        catalogCategoryRepository.save(catalogCategory);
    }

    @Override
    public CatalogCategory getCatalogCategoryByIdAndBusinessId(Long id, Long businessId) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        if (businessId == null) {
            throw new IllegalArgumentException("businessId is null");
        }

        return catalogCategoryRepository.findByIdAndBusinessIdAndIsDeletedFalse(id, businessId)
                .orElseThrow(() -> new CatalogCategoryNotFound("Catalog category not found"));
    }

    @Override
    public List<CatalogCategory> getCatalogCategoriesByBusinessId(Long businessId) {
        if (businessId == null) {
            throw new IllegalArgumentException("businessId is null");
        }

        return catalogCategoryRepository.findAllByBusinessIdAndIsDeletedFalseOrderByOrderIndexAsc(businessId);
    }

    @Override
    public boolean hasCategoryWithThisIndex(Long businessId, Integer orderIndex) {
        if(orderIndex == null || businessId == null) {
            throw new IllegalArgumentException("orderIndex or businessId is null");
        }

        return catalogCategoryRepository.findByBusinessIdAndOrderIndexAndIsDeletedFalse(businessId, orderIndex).isPresent();
    }

    @Override
    public Integer getNextOrderIndex(Long businessId) {
        if(businessId == null) {
            throw new IllegalArgumentException("businessId is null");
        }

        List<CatalogCategory> catalogCategoriesByBusinessId = getCatalogCategoriesByBusinessId(businessId);
        return catalogCategoriesByBusinessId.size();
    }

    @Override
    public boolean canBeDeleted(CatalogCategory category) {
        return catalogItemRepository.countByCategoryAndIsDeletedFalse(category) == 0;
    }

    @Override
    public void delete(CatalogCategory catalogCategory) {
        catalogCategory.setIsDeleted(true);
        catalogCategoryRepository.save(catalogCategory);
    }
}
