package com.shabanaj.beloyal.features.catalogItems.service;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemStatusChangeResponse;

public interface CatalogItemLifecycleService {
    CatalogItemStatusChangeResponse activate(Long businessId, Long id);
    CatalogItemStatusChangeResponse deactivate(Long businessId, Long id);
    CatalogItemStatusChangeResponse restore(Long businessId, Long id);
    void delete(Long businessId, Long id);
}
