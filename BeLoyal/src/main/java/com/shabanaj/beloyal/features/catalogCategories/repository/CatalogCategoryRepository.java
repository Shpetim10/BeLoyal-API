package com.shabanaj.beloyal.features.catalogCategories.repository;

import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CatalogCategoryRepository extends JpaRepository<CatalogCategory, Long> {
    Optional<CatalogCategory> findByIdAndIsDeletedFalse(Long id);
    Optional<CatalogCategory> findByIdAndBusinessIdAndIsDeletedFalse(Long id, Long businessId);
    List<CatalogCategory> findAllByBusinessIdAndIsDeletedFalseOrderByOrderIndexAsc(Long businessId);
    Optional<CatalogCategory> findByBusinessIdAndOrderIndexAndIsDeletedFalse(Long businessId, Integer orderIndex);

    List<CatalogCategory> findAllByBusinessIdAndIsDeletedTrueOrderByUpdatedAtDesc(Long businessId);
    Optional<CatalogCategory> findByIdAndBusinessId(Long id, Long businessId);
}
