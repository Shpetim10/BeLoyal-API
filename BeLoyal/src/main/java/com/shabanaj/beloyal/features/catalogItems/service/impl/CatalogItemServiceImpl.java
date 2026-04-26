package com.shabanaj.beloyal.features.catalogItems.service.impl;

import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogItemServiceImpl implements CatalogItemService {
    private final CatalogItemRepository catalogItemRepository;

    @Override
    public void save(CatalogItem catalogItem) {
        catalogItemRepository.save(catalogItem);
    }

    @Override
    public Integer getNextOrderIndex(Business business, CatalogCategory catalogCategory) {

        return catalogItemRepository.countByBusinessAndCategoryAndIsDeletedFalse(business, catalogCategory);

    }

    @Override
    public List<CatalogItem> getCatalogItems(Business business, CatalogCategory catalogCategory) {
        return catalogItemRepository.findAllByBusinessAndCategory(business, catalogCategory);
    }
}
