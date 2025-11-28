package com.shabanaj.beloyal.Repository;

import com.shabanaj.beloyal.Entity.CustomerProfile;
import com.shabanaj.beloyal.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long>{
    Optional<CustomerProfile> findByUser(User user);
    Optional<CustomerProfile> findByReferralCode(String phoneNumber);
}
