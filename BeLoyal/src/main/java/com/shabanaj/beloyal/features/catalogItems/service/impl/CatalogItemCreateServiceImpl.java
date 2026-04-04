package com.shabanaj.beloyal.features.catalogItems.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryService;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemCreateRequest;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemCreateResponse;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemCreateService;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogItemCreateServiceImpl implements CatalogItemCreateService {
    private final CatalogItemService catalogItemService;
    private final CatalogCategoryService catalogCategoryService;
    private final BusinessService businessService;

    @Override
    @Transactional
    public CatalogItemCreateResponse create(Long businessId, Long categoryId, CatalogItemCreateRequest catalogItemCreateRequest) {
        // validate arguments
        validateArguments(businessId, categoryId, catalogItemCreateRequest);

        // find business
        Business business = businessService.getActiveBusinessById(businessId);

        // find category(allow inactive categories)
        CatalogCategory category= catalogCategoryService.getCatalogCategoryByIdAndBusinessId(categoryId, businessId);

        // create catalog category
        CatalogItem catalogItem = CatalogItem.builder()
                .business(business)
                .category(category)
                .name(catalogItemCreateRequest.getName())
                .description(catalogItemCreateRequest.getDescription())
                .price(catalogItemCreateRequest.getPrice())
                .currencyCode(catalogItemCreateRequest.getCurrency())
                .type(catalogItemCreateRequest.getType())
                .unit(catalogItemCreateRequest.getUnit())
                .status(CatalogStatus.ACTIVE)
                .imageKey(catalogItemCreateRequest.getImageKey())
                .imageUrl(catalogItemCreateRequest.getImageUrl())
                .orderIndex(catalogItemService.getNextOrderIndex(business, category))
                .build();

        // persist
        catalogItemService.save(catalogItem);

        // return response
        return createResponse(catalogItem);
    }

    // helpers
    private void validateArguments(Long businessId, Long categoryId, CatalogItemCreateRequest catalogItemCreateRequest) {
        if(businessId == null || categoryId == null || catalogItemCreateRequest == null){
            throw new IllegalArgumentException("Arguments cannot be null");
        }
    }

    private CatalogItemCreateResponse createResponse(CatalogItem catalogItem){
        if(catalogItem == null){
            throw new IllegalArgumentException("CatalogItem cannot be null");
        }

        return CatalogItemCreateResponse.builder()
                .name(catalogItem.getName())
                .description(catalogItem.getDescription())
                .price(catalogItem.getPrice())
                .currencyCode(catalogItem.getCurrencyCode().getCode())
                .type(catalogItem.getType().getName())
                .unit(catalogItem.getUnit())
                .status(catalogItem.getStatus().getName())
                .imageUrl(catalogItem.getImageUrl())
                .orderIndex(catalogItem.getOrderIndex())
                .createdAt(catalogItem.getCreatedAt())
                .updatedAt(catalogItem.getUpdatedAt())
                .build();
    }
}
