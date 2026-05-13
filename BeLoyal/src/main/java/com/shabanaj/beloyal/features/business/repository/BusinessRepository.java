package com.shabanaj.beloyal.features.business.repository;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    List<Business> findAllByBusinessStatus(BusinessStatus businessStatus);
    Optional<Business> findBusinessByVatId(String vatId);
    List<Business> getBusinessesByBusinessStatus(BusinessStatus businessStatus);
    boolean existsByVatIdIgnoreCase(String vatId);
    boolean existsByVatIdIgnoreCaseAndIdNot(String vatId, Long id);

    // override the find all
    @Query(
            "SELECT b FROM Business b " +
                    "ORDER BY b.createdAt"
    )
    List<Business> findAllOrderedByCreatedAt();
}
