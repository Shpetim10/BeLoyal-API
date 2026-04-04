package com.shabanaj.beloyal.features.catalogCategories.repository;

import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CatalogCategoryRepository extends JpaRepository<CatalogCategory, Long> {
    Optional<CatalogCategory> findById(Long id);
    Optional<CatalogCategory> findByIdAndBusinessId(Long id, Long businessId);
    List<CatalogCategory> findAllByBusinessId(Long businessId);
    Optional<CatalogCategory> findByBusinessIdAndOrderIndex(Long businessId, Integer orderIndex);
}
