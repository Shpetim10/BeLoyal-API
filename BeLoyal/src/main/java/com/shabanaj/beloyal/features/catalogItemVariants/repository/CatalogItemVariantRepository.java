package com.shabanaj.beloyal.features.catalogItemVariants.repository;

import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CatalogItemVariantRepository extends JpaRepository<CatalogItemVariant, Long> {

    /** Used for list endpoint — active variants ordered for display. */
    List<CatalogItemVariant> findByCatalogItemIdAndIsDeletedFalseOrderByOrderIndexAsc(Long catalogItemId);

    /** Used to find a variant that belongs to a specific catalog item (active or deleted). */
    Optional<CatalogItemVariant> findByIdAndCatalogItemId(Long id, Long catalogItemId);

    Optional<CatalogItemVariant> findByIdAndCatalogItemIdAndIsDeletedFalse(Long id, Long catalogItemId);

    /** Count active variants — drives the next order index on create. */
    Integer countByCatalogItemIdAndIsDeletedFalse(Long catalogItemId);

    // ── kept for backwards-compat (used by CreateService) ──────────────────
    List<CatalogItemVariant> findByCatalogItemIdAndIsDeletedFalse(Long catalogItemId);

    List<CatalogItemVariant> findByCatalogItemIdInAndIsDeletedFalseOrderByOrderIndexAsc(java.util.Collection<Long> itemIds);
}
