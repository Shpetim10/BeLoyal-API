package com.shabanaj.beloyal.features.catalogItems.service;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemDetailResponse;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemShortResponse;

import java.util.List;

public interface CatalogItemViewService {
    List<CatalogItemShortResponse> getItemsByCategory(Long businessId, Long categoryId);
    CatalogItemDetailResponse getItemDetails(Long businessId, Long itemId);
    List<CatalogItemShortResponse> getTrashItemsByBusiness(Long businessId);
}
