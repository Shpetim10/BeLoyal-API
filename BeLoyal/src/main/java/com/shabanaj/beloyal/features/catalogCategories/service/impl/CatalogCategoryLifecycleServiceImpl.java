package com.shabanaj.beloyal.features.catalogCategories.service.impl;

import com.shabanaj.beloyal.common.Exception.CatalogCategoryHasItems;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryStatusChangeResponse;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryLifecycleService;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryService;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogCategoryLifecycleServiceImpl implements CatalogCategoryLifecycleService {
    private final CatalogCategoryService  catalogCategoryService;
    @Override
    public CatalogCategoryStatusChangeResponse activate(Long businessId, Long id) {
        // validate input
        validateInput(businessId, id);

        // find the category
        CatalogCategory category= catalogCategoryService.getCatalogCategoryByIdAndBusinessId(id,businessId);

        // activate
        category.activate();

        //persist
        catalogCategoryService.save(category);

        return mapToResponse(category);
    }

    @Override
    public CatalogCategoryStatusChangeResponse deactivate(Long businessId, Long id) {
        // validate input
        validateInput(businessId, id);

        // find the category
        CatalogCategory category= catalogCategoryService.getCatalogCategoryByIdAndBusinessId(id,businessId);

        // activate
        category.deactivate();

        //persist
        catalogCategoryService.save(category);

        return mapToResponse(category);
    }

    @Override
    public void delete(Long businessId, Long id) {
        // validate input
        validateInput(businessId, id);

        // find the category
        CatalogCategory category= catalogCategoryService.getCatalogCategoryByIdAndBusinessId(id,businessId);

        // check if it has items inside
        if(!catalogCategoryService.canBeDeleted(businessId)){
            throw new CatalogCategoryHasItems("This Category cannot be deleted because it has items inside");
        }

        // delete
        catalogCategoryService.delete(category);
    }

    // helpers
    private void validateInput(Long businessId, Long id){
        if(businessId == null){
            throw new IllegalArgumentException("businessId cannot be null");
        }

        if(id == null){
            throw new IllegalArgumentException("id cannot be null");
        }
    }

    private CatalogCategoryStatusChangeResponse mapToResponse(CatalogCategory catalogCategory){
        return CatalogCategoryStatusChangeResponse.builder()
                .id(catalogCategory.getId())
                .name(catalogCategory.getName())
                .description(catalogCategory.getDescription())
                .status(catalogCategory.getStatus().getName())
                .orderIndex(catalogCategory.getOrderIndex())
                .createdAt(catalogCategory.getCreatedAt())
                .build();
    }
}
