package com.shabanaj.beloyal.features.catalogItems.repository;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {
    Integer countByBusinessAndCategory(Business business, CatalogCategory category);
    Integer countByCategoryAndIsDeletedFalse(CatalogCategory category);
    List<CatalogItem> findAllByBusinessAndCategory(Business business, CatalogCategory catalogCategory);
    Integer countByBusinessAndCategoryAndIsDeletedFalse(Business business, CatalogCategory category);
    Integer countByBusinessIdAndCategoryIdAndIsDeletedFalse(Long businessId, Long categoryId);

    List<CatalogItem> findAllByBusinessIdAndCategoryIdAndIsDeletedFalseOrderByOrderIndexAsc(Long businessId, Long categoryId);
    List<CatalogItem> findAllByBusinessIdAndIsDeletedFalse(Long businessId);
    java.util.Optional<CatalogItem> findByIdAndBusinessIdAndIsDeletedFalse(Long id, Long businessId);

    List<CatalogItem> findAllByBusinessIdAndIsDeletedTrueOrderByUpdatedAtDesc(Long businessId);
    java.util.Optional<CatalogItem> findByIdAndBusinessId(Long id, Long businessId);

    // used for order-index recompaction after a category move
    List<CatalogItem> findAllByCategoryAndIsDeletedFalseOrderByOrderIndexAsc(CatalogCategory category);
    Integer countByCategoryIdAndIsDeletedFalse(Long categoryId);

    @Modifying
    @Query("DELETE FROM CatalogItem ci WHERE ci.business.id = :businessId")
    void deleteByBusinessId(@Param("businessId") Long businessId);
}
