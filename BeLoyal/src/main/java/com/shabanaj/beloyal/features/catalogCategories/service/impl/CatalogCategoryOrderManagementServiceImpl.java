package com.shabanaj.beloyal.features.catalogCategories.service.impl;

import com.shabanaj.beloyal.common.Exception.CatalogCategoryNotFound;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryOrderUpdateRequest;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryViewDto;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryOrderManagementService;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogCategoryOrderManagementServiceImpl implements CatalogCategoryOrderManagementService {

    private final CatalogCategoryRepository catalogCategoryRepository;

    @Override
    @Transactional
    public List<CatalogCategoryViewDto> updateOrder(Long businessId, CatalogCategoryOrderUpdateRequest request) {
        List<CatalogCategoryViewDto> result = new ArrayList<>();

        for (CatalogCategoryOrderUpdateRequest.CategoryOrderDto dto : request.getCategoryOrders()) {
            CatalogCategory category = catalogCategoryRepository.findByIdAndBusinessId(dto.getCategoryId(), businessId)
                    .orElseThrow(() -> new CatalogCategoryNotFound("Catalog Category with id " + dto.getCategoryId() + " not found for this business."));

            category.setOrderIndex(dto.getOrderIndex());
            catalogCategoryRepository.save(category);

            result.add(
                CatalogCategoryViewDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .orderIndex(category.getOrderIndex())
                    .status(category.getStatus().getName())
                    .createdAt(category.getCreatedAt())
                    .build()
            );
        }

        return result;
    }
}
