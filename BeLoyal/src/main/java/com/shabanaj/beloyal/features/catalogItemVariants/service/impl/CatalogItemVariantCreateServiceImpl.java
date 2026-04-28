package com.shabanaj.beloyal.features.catalogItemVariants.service.impl;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantCreateRequest;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantCreateResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantCreateService;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantOrderManagementService;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantService;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemService;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;
import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogItemVariantCreateServiceImpl implements CatalogItemVariantCreateService {
    private final CatalogItemService catalogItemService;
    private final CatalogItemVariantService catalogItemVariantService;
    private final CatalogItemVariantOrderManagementService  catalogItemVariantOrderManagementService;
    @Override
    public CatalogItemVariantCreateResponse create(Long businessId, Long catalogItemId, CatalogItemVariantCreateRequest request) {
        // validate request
        validateRequest(businessId, catalogItemId, request);

        // find catalog item
        CatalogItem catalogItem= catalogItemService.getByIdAndBusinessIdAndIsDeletedFalse(catalogItemId, businessId);

        // save
        CatalogItemVariant catalogItemVariant = CatalogItemVariant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .catalogItem(catalogItem)
                .priceOverride(request.getPriceOverride()==null?null:request.getPriceOverride())
                .orderIndex(catalogItemVariantOrderManagementService.getNextOrderIndex(catalogItemId))
                .status(CatalogStatus.ACTIVE)
                .isDeleted(false)
                .build();

        catalogItemVariantService.save(catalogItemVariant);

        return mapToResponse(catalogItemVariant);
    }

    private void validateRequest(Long businessId, Long catalogItemId, CatalogItemVariantCreateRequest catalogItemVariantCreateRequest){
        if(businessId==null||catalogItemId==null||catalogItemVariantCreateRequest==null){
            throw new IllegalArgumentException("parameters cannot be null");
        }
    }

    private CatalogItemVariantCreateResponse mapToResponse(CatalogItemVariant  catalogItemVariant){
        if (catalogItemVariant==null){
            throw new IllegalArgumentException("parameters cannot be null");
        }
        return CatalogItemVariantCreateResponse.builder()
                .name(catalogItemVariant.getName())
                .description(catalogItemVariant.getDescription())
                .price(catalogItemVariant.getPriceOverride())
                .status(catalogItemVariant.getStatus().getName())
                .orderIndex(catalogItemVariant.getOrderIndex())
                .build();
    }
}
