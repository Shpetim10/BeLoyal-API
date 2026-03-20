package com.shabanaj.beloyal.features.loyaltyAccount.repository;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findByCustomerProfileAndBusiness(CustomerProfile customerProfile, Business business);
    Optional<LoyaltyAccount> findByCustomerProfileId(Long customerProfileId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoyaltyAccount> findWithLockById(Long id);
}
