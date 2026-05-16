package com.shabanaj.beloyal.features.userProfiles.customer.repository;

import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long>{
    Optional<CustomerProfile> findByUser(User user);
    Optional<CustomerProfile> findByReferralCode(String phoneNumber);
    Optional<CustomerProfile> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM CustomerProfile cp WHERE cp.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
