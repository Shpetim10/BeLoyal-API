package com.shabanaj.beloyal.features.catalogCategories.service.impl;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.common.Exception.CatalogCategoryNotFound;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryUpdateRequest;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryViewDto;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryUpdateService;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogCategoryUpdateServiceImpl implements CatalogCategoryUpdateService {

    private final CatalogCategoryRepository  catalogCategoryRepository;

    @Override
    @Transactional
    public CatalogCategoryViewDto update(Long businessId, Long id, CatalogCategoryUpdateRequest request) {
        // 1. Fetch
        CatalogCategory category = catalogCategoryRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new CatalogCategoryNotFound("Catalog Category with id " + id + " not found for this business."));

        // 2. Map provided fields
        // Name
        if (request.getName() == null || request.getName().isPresent()) {
            String newName = request.getName() == null ? null : request.getName().get();
            if (newName == null || newName.trim().isEmpty()) {
                throw new BadRequestException("Category name is mandatory and cannot be empty.");
            }
            if (newName.length() > 120) {
                throw new BadRequestException("The name of category is too long");
            }
            category.setName(newName);
        }

        // Description
        if (request.getDescription() == null) {
            category.setDescription(null);
        } else if (request.getDescription().isPresent()) {
            String desc = request.getDescription().get();
            if (desc != null && desc.trim().isEmpty()) {
                desc = null; // Treat empty string as request to clear the description
            }
            if (desc != null && desc.length() > 300) {
                throw new BadRequestException("The category description should not exceed 300 characters");
            }
            category.setDescription(desc);
        }

        // Order Index
        if (request.getOrderIndex() == null || request.getOrderIndex().isPresent()) {
            Integer newOrderIndex = request.getOrderIndex() == null ? null : request.getOrderIndex().get();
            if (newOrderIndex == null) {
                throw new BadRequestException("Category order index is mandatory and cannot be null.");
            }
            category.setOrderIndex(newOrderIndex);
        }

        // Status
        if (request.getStatus() == null || request.getStatus().isPresent()) {
            com.shabanaj.beloyal.model.Enums.CatalogStatus newStatus = request.getStatus() == null ? null : request.getStatus().get();
            if (newStatus == null) {
                throw new BadRequestException("Category status is mandatory and cannot be null.");
            }
            category.setStatus(newStatus);
        }


        // 3. Save
        catalogCategoryRepository.save(category);

        // 4. Return DTO
        return CatalogCategoryViewDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .orderIndex(category.getOrderIndex())
                .status(category.getStatus().getName())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
