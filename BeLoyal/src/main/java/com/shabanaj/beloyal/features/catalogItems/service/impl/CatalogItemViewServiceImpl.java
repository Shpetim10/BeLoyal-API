package com.shabanaj.beloyal.features.catalogItems.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemDetailResponse;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemShortResponse;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemViewService;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogItemViewServiceImpl implements CatalogItemViewService {

    private final CatalogItemRepository catalogItemRepository;
    private final BusinessService businessService;

    @Override
    public List<CatalogItemShortResponse> getItemsByCategory(Long businessId, Long categoryId) {
        businessService.getActiveBusinessById(businessId); // Ensure business exists

        List<CatalogItem> items = catalogItemRepository.findAllByBusinessIdAndCategoryIdAndIsDeletedFalseOrderByOrderIndexAsc(businessId, categoryId);

        return items.stream()
                .map(item -> CatalogItemShortResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .status(item.getStatus().getName())
                        .categoryName(item.getCategory().getName())
                        .price(item.getPrice())
                        .orderIndex(item.getOrderIndex())
                        .imageUrl(item.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<CatalogItemShortResponse> getItemsByBusiness(Long businessId) {
        businessService.getActiveBusinessById(businessId); // Ensure business exists

        List<CatalogItem> items = catalogItemRepository.findAllByBusinessIdAndIsDeletedFalse(businessId);

        return items.stream()
                .map(item -> CatalogItemShortResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .status(item.getStatus().getName())
                        .categoryId(item.getCategory().getId())
                        .categoryName(item.getCategory().getName())
                        .price(item.getPrice())
                        .currencyCode(item.getCurrencyCode().name())
                        .orderIndex(item.getOrderIndex())
                        .imageUrl(item.getImageUrl())
                        .isDeleted(item.getIsDeleted())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CatalogItemDetailResponse getItemDetails(Long businessId, Long itemId) {
        businessService.getActiveBusinessById(businessId); // Ensure business exists

        CatalogItem item = catalogItemRepository.findByIdAndBusinessId(itemId, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Catalog item not found or is deleted"));

        return CatalogItemDetailResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .status(item.getStatus().name())
                .categoryName(item.getCategory().getName())
                .categoryId(item.getCategory().getId())
                .price(item.getPrice())
                .type(item.getType().name())
                .currencyCode(item.getCurrencyCode().name())
                .unit(item.getUnit())
                .orderIndex(item.getOrderIndex())
                .imageUrl(item.getImageUrl())
                .isDeleted(item.getIsDeleted())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    @Override
    public List<CatalogItemShortResponse> getTrashItemsByBusiness(Long businessId) {
        businessService.getActiveBusinessById(businessId); // Ensure business exists

        List<CatalogItem> items = catalogItemRepository.findAllByBusinessIdAndIsDeletedTrueOrderByUpdatedAtDesc(businessId);

        return items.stream()
                .map(item -> CatalogItemShortResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .status(item.getStatus().name())
                        .categoryId(item.getCategory().getId())
                        .categoryName(item.getCategory().getName())
                        .price(item.getPrice())
                        .currencyCode(item.getCurrencyCode().name())
                        .orderIndex(item.getOrderIndex())
                        .imageUrl(item.getImageUrl())
                        .isDeleted(item.getIsDeleted())
                        .build())
                .collect(Collectors.toList());
    }
}
