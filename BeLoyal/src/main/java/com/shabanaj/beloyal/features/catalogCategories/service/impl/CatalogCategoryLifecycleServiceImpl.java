package com.shabanaj.beloyal.features.catalogCategories.service.impl;

import com.shabanaj.beloyal.common.Exception.CatalogCategoryHasItems;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryStatusChangeResponse;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryLifecycleService;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryService;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogCategoryLifecycleServiceImpl implements CatalogCategoryLifecycleService {
    private final CatalogCategoryService  catalogCategoryService;
    private final CatalogCategoryRepository catalogCategoryRepository;

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
    public CatalogCategoryStatusChangeResponse restore(Long businessId, Long id) {
        // validate input
        validateInput(businessId, id);

        // find the trashed category via repository (service filters deleted)
        CatalogCategory category = catalogCategoryRepository.findByIdAndBusinessId(id, businessId)
            .orElseThrow(() -> new IllegalArgumentException("Trashed Catalog category not found"));

        category.setIsDeleted(false);
        category.setOrderIndex(catalogCategoryService.getNextOrderIndex(businessId));
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
        if(!catalogCategoryService.canBeDeleted(category)){
            throw new CatalogCategoryHasItems("This Category cannot be deleted because it has active items inside");
        }

        // delete (which is now soft delete)
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
