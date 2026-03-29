package com.shabanaj.beloyal.features.catalogCategories.service.impl;

import com.shabanaj.beloyal.common.Exception.CatalogCategoryNotFound;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryService;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogCategoryServiceImpl implements CatalogCategoryService {
    private final CatalogCategoryRepository catalogCategoryRepository;


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

        return catalogCategoryRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new CatalogCategoryNotFound("Catalog category not found"));
    }

    @Override
    public List<CatalogCategory> getCatalogCategoriesByBusinessId(Long businessId) {
        if (businessId == null) {
            throw new IllegalArgumentException("businessId is null");
        }

        return catalogCategoryRepository.findAllByBusinessId(businessId);
    }
}
