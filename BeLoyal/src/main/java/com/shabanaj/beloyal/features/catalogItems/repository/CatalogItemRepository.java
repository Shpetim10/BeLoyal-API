package com.shabanaj.beloyal.features.catalogItems.repository;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {
    Integer countByBusinessAndCategory(Business business, CatalogCategory category);
    Integer countByCategoryAndIsDeletedFalse(CatalogCategory category);
    List<CatalogItem> findAllByBusinessAndCategory(Business business, CatalogCategory catalogCategory);

    List<CatalogItem> findAllByBusinessIdAndCategoryIdAndIsDeletedFalseOrderByOrderIndexAsc(Long businessId, Long categoryId);
    java.util.Optional<CatalogItem> findByIdAndBusinessIdAndIsDeletedFalse(Long id, Long businessId);

    List<CatalogItem> findAllByBusinessIdAndIsDeletedTrueOrderByUpdatedAtDesc(Long businessId);
    java.util.Optional<CatalogItem> findByIdAndBusinessId(Long id, Long businessId);
}
