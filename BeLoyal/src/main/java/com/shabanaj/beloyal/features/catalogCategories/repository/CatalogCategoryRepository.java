package com.shabanaj.beloyal.features.catalogCategories.repository;

import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CatalogCategoryRepository extends JpaRepository<CatalogCategory, Long> {
    Optional<CatalogCategory> findByIdAndIsDeletedFalse(Long id);
    Optional<CatalogCategory> findByIdAndBusinessIdAndIsDeletedFalse(Long id, Long businessId);
    List<CatalogCategory> findAllByBusinessIdAndIsDeletedFalseOrderByOrderIndexAsc(Long businessId);
    List<CatalogCategory> findAllByBusinessIdAndStatusAndIsDeletedFalseOrderByOrderIndexAsc(Long businessId, CatalogStatus status);
    Optional<CatalogCategory> findByBusinessIdAndOrderIndexAndIsDeletedFalse(Long businessId, Integer orderIndex);

    List<CatalogCategory> findAllByBusinessIdAndIsDeletedTrueOrderByUpdatedAtDesc(Long businessId);
    Optional<CatalogCategory> findByIdAndBusinessId(Long id, Long businessId);

    @Modifying
    @Query("DELETE FROM CatalogCategory cc WHERE cc.business.id = :businessId")
    void deleteByBusinessId(@Param("businessId") Long businessId);
}
