package com.shabanaj.beloyal.features.catalogItemVariants.service.impl;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantService;
import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogItemVariantServiceImpl implements CatalogItemVariantService {

    private final CatalogItemVariantRepository catalogItemVariantRepository;

    @Override
    public void save(CatalogItemVariant catalogItemVariant) {
        catalogItemVariantRepository.save(catalogItemVariant);
    }

    @Override
    public void saveAll(List<CatalogItemVariant> variants) {
        catalogItemVariantRepository.saveAll(variants);
    }

    @Override
    public List<CatalogItemVariant> getCatalogItemVariantsByCatalogItemIdAndIsDeletedFalse(Long catalogItemId) {
        return catalogItemVariantRepository.findByCatalogItemIdAndIsDeletedFalse(catalogItemId);
    }

    @Override
    public List<CatalogItemVariant> getCatalogItemVariantsByCatalogItemIdAndIsDeletedFalseOrderedByIndex(Long catalogItemId) {
        return catalogItemVariantRepository.findByCatalogItemIdAndIsDeletedFalseOrderByOrderIndexAsc(catalogItemId);
    }

    @Override
    public CatalogItemVariant getByIdAndCatalogItemId(Long variantId, Long catalogItemId) {
        return catalogItemVariantRepository.findByIdAndCatalogItemId(variantId, catalogItemId)
                .orElseThrow(() -> new BadRequestException(
                        "Catalog item variant not found for catalog item " + catalogItemId));
    }
}

