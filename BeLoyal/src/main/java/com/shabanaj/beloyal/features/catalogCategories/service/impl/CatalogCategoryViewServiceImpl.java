package com.shabanaj.beloyal.features.catalogCategories.service.impl;

import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryViewDto;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryService;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryViewService;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogCategoryViewServiceImpl implements CatalogCategoryViewService {
    private final CatalogCategoryService catalogCategoryService;

    @Override
    public CatalogCategoryViewDto viewCatalogCategory(Long id, Long businessId) {
        // get the catalog category or throw
        CatalogCategory catalogCategory= catalogCategoryService.getCatalogCategoryByIdAndBusinessId(id, businessId);

        // map to dto
        return CatalogCategoryViewDto.builder()
                .id(catalogCategory.getId())
                .name(catalogCategory.getName())
                .description(catalogCategory.getDescription())
                .orderIndex(catalogCategory.getOrderIndex())
                .status(catalogCategory.getStatus().getName())
                .createdAt(catalogCategory.getCreatedAt())
                .build();
    }

    @Override
    public List<CatalogCategoryViewDto> viewCatalogCategories(Long businessId) {
        // get category list
        List<CatalogCategory> catalogCategories= catalogCategoryService.getCatalogCategoriesByBusinessId(businessId);

        // map to dto
        return catalogCategories
                .stream()
                .map(e-> CatalogCategoryViewDto.builder()
                        .id(e.getId())
                        .name(e.getName())
                        .description(e.getDescription())
                        .orderIndex(e.getOrderIndex())
                        .status(e.getStatus().getName())
                        .createdAt(e.getCreatedAt())
                        .build())
                .toList();
    }
}
