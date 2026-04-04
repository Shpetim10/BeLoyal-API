package com.shabanaj.beloyal.features.catalogCategories.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryCreateRequest;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryCreateResponse;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryCreateService;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogCategoryCreateServiceImpl implements CatalogCategoryCreateService {
    private final CatalogCategoryService catalogCategoryService;
    private final BusinessService businessService;

    @Override
    @Transactional
    public CatalogCategoryCreateResponse create(Long businessId, CatalogCategoryCreateRequest catalogCategoryCreateRequest) {
        if(catalogCategoryCreateRequest == null) {
            throw new IllegalArgumentException("catalogCategoryCreateRequest is null");
        }
        // find business
        Business business= businessService.getActiveBusinessById(businessId);

        // calculate next index
        Integer orderIndex= catalogCategoryService.getNextOrderIndex(businessId);

        // create category object
        CatalogCategory catalogCategory= CatalogCategory.builder()
                .business(business)
                .name(catalogCategoryCreateRequest.getName())
                .description(catalogCategoryCreateRequest.getDescription())
                .orderIndex(orderIndex)
                .status(CatalogStatus.ACTIVE)
                .build();

        // persist
        catalogCategoryService.save(catalogCategory);

        // map to response
        return mapToResponse(catalogCategory);
    }

    private CatalogCategoryCreateResponse mapToResponse(CatalogCategory catalogCategory) {
        if(catalogCategory == null) {
            throw new IllegalArgumentException("catalogCategory is null");
        }

        return CatalogCategoryCreateResponse.builder()
                .id(catalogCategory.getId())
                .name(catalogCategory.getName())
                .description(catalogCategory.getDescription())
                .orderIndex(catalogCategory.getOrderIndex())
                .status(catalogCategory.getStatus().getName())
                .build();
    }
}
