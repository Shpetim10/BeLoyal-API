package com.shabanaj.beloyal.features.loyaltyAccount.repository;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findByCustomerProfileAndBusiness(CustomerProfile customerProfile, Business business);
    Optional<LoyaltyAccount> findByCustomerProfileId(Long customerProfileId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoyaltyAccount> findWithLockById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoyaltyAccount> findWithLockByCustomerProfileIdAndBusinessId(Long customerProfileId, Long businessId);

    List<LoyaltyAccount> findLoyaltyCardsByCustomerProfile(CustomerProfile customerProfile);

    @Query("SELECT la FROM LoyaltyAccount la JOIN FETCH la.business WHERE la.customerProfile.id = :profileId")
    List<LoyaltyAccount> findAllWithBusinessByCustomerProfileId(@Param("profileId") Long profileId);

    long countByBusinessId(Long businessId);

    @Modifying
    @Query("DELETE FROM LoyaltyAccount la WHERE la.business.id = :businessId")
    void deleteByBusinessId(@Param("businessId") Long businessId);

    @Modifying
    @Query("DELETE FROM LoyaltyAccount la WHERE la.customerProfile.id = :customerProfileId")
    void deleteByCustomerProfileId(@Param("customerProfileId") Long customerProfileId);

    @Query("SELECT COALESCE(SUM(la.lifetimeEarned), 0) FROM LoyaltyAccount la WHERE la.customerProfile.user.id = :userId")
    long sumLifetimeEarnedByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(la.lifetimeRedeemed), 0) FROM LoyaltyAccount la WHERE la.customerProfile.user.id = :userId")
    long sumLifetimeRedeemedByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(la.lifetimeExpired), 0) FROM LoyaltyAccount la WHERE la.customerProfile.user.id = :userId")
    long sumLifetimeExpiredByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(la.availablePoints), 0) FROM LoyaltyAccount la WHERE la.customerProfile.user.id = :userId")
    long sumAvailablePointsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(la) FROM LoyaltyAccount la WHERE la.customerProfile.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
