package com.shabanaj.beloyal.features.catalogItems.service;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemDetailResponse;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemUpdateRequest;

public interface CatalogItemUpdateService {
    CatalogItemDetailResponse update(Long businessId, Long itemId, CatalogItemUpdateRequest request);
}
