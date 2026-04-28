package com.shabanaj.beloyal.features.catalogItemVariants.service.impl;

import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantOrderManagementService;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogItemVariantOrderManagementServiceImpl implements CatalogItemVariantOrderManagementService {
    private final CatalogItemVariantService catalogItemVariantService;
    private final CatalogItemVariantRepository  catalogItemVariantRepository;
    @Override
    public Integer getNextOrderIndex(Long catalogItemId) {
        if(catalogItemId == null){
            throw new IllegalArgumentException("catalogItemId is null");
        }

        return catalogItemVariantRepository.countByCatalogItemIdAndIsDeletedFalse(catalogItemId);
    }
}
