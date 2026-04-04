package com.shabanaj.beloyal.features.catalogItems.service.impl;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemStatusChangeResponse;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemLifecycleService;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogItemLifecycleServiceImpl implements CatalogItemLifecycleService {

    private final CatalogItemRepository catalogItemRepository;

    @Override
    @Transactional
    public CatalogItemStatusChangeResponse activate(Long businessId, Long id) {
        CatalogItem item = getCatalogItem(businessId, id);
        item.setStatus(CatalogStatus.ACTIVE);
        catalogItemRepository.save(item);
        return createResponse(item);
    }

    @Override
    @Transactional
    public CatalogItemStatusChangeResponse deactivate(Long businessId, Long id) {
        CatalogItem item = getCatalogItem(businessId, id);
        item.setStatus(CatalogStatus.INACTIVE);
        catalogItemRepository.save(item);
        return createResponse(item);
    }

    @Override
    @Transactional
    public CatalogItemStatusChangeResponse restore(Long businessId, Long id) {
        CatalogItem item = getCatalogItem(businessId, id);
        item.setIsDeleted(false);
        catalogItemRepository.save(item);
        return createResponse(item);
    }

    @Override
    @Transactional
    public void delete(Long businessId, Long id) {
        CatalogItem item = getCatalogItem(businessId, id);
        item.setIsDeleted(true);
        catalogItemRepository.save(item);
    }

    private CatalogItem getCatalogItem(Long businessId, Long id) {
        return catalogItemRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Catalog item not found for this business"));
    }

    private CatalogItemStatusChangeResponse createResponse(CatalogItem item) {
        return CatalogItemStatusChangeResponse.builder()
                .id(item.getId())
                .status(item.getStatus().name())
                .isDeleted(item.getIsDeleted())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
