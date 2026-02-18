package com.shabanaj.beloyal.Repository;

import com.shabanaj.beloyal.Entity.Business;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Enums.BusinessStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    List<Business> findAllByBusinessStatus(BusinessStatus businessStatus);
    Optional<Business> findBusinessByVatId(String vatId);
}
