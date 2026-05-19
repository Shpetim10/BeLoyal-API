package com.shabanaj.beloyal.features.customerCoupon.repository;

import com.shabanaj.beloyal.model.Entity.CustomerCoupon;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerCouponRepository extends JpaRepository<CustomerCoupon, Long> {

    boolean existsByCouponId(Long couponId);

    int countByCouponIdAndCustomerProfileId(Long couponId, Long customerProfileId);

    List<CustomerCoupon> findAllByCustomerProfileIdOrderByCreatedAtDesc(Long customerProfileId);

    @Query(
            "SELECT COUNT(cc) FROM CustomerCoupon cc WHERE cc.customerProfile = :customerProfile AND cc.status = :couponStatus"
    )
    long countAllByCustomerProfileAndStatus(
            @Param("customerProfile") CustomerProfile customerProfile,
            @Param("couponStatus") CustomerCouponStatus couponStatus
    );

    /**
     * Counts REDEEMED owned coupons that have not yet passed their snapshot expiry.
     * Used for the customer active-coupon stat so that expired-but-not-scanned rows
     * are not counted as active.
     */
    @Query("""
            SELECT COUNT(cc) FROM CustomerCoupon cc
            WHERE cc.customerProfile = :customerProfile
              AND cc.status = :status
              AND (cc.expiresAt IS NULL OR cc.expiresAt > :now)
            """)
    long countActiveByCustomerProfile(
            @Param("customerProfile") CustomerProfile customerProfile,
            @Param("status") CustomerCouponStatus status,
            @Param("now") java.time.LocalDateTime now
    );

    List<CustomerCoupon> findAllByCustomerProfileIdAndBusinessIdOrderByCreatedAtDesc(Long customerProfileId, Long businessId);

    Optional<CustomerCoupon> findByIdAndCustomerProfileId(Long id, Long customerProfileId);

    Optional<CustomerCoupon> findByIdAndBusinessId(Long id, Long businessId);

    Optional<CustomerCoupon> findByQrCode(String qrCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cc FROM CustomerCoupon cc WHERE cc.qrCode = :qrCode")
    Optional<CustomerCoupon> findWithLockByQrCode(@Param("qrCode") String qrCode);

    @Query("SELECT cc FROM CustomerCoupon cc " +
           "JOIN FETCH cc.coupon c " +
           "JOIN FETCH cc.business b " +
           "WHERE cc.customerProfile.id = :profileId " +
           "ORDER BY cc.createdAt DESC")
    List<CustomerCoupon> findAllWithCouponByCustomerProfileId(@Param("profileId") Long profileId);

    Optional<CustomerCoupon> findByCouponIdAndCustomerProfileId(Long couponId, Long customerProfileId);

    Optional<CustomerCoupon> findTopByCouponIdAndCustomerProfileIdOrderByCreatedAtDesc(Long couponId, Long customerProfileId);

    List<CustomerCoupon> findAllByCouponIdAndCustomerProfileIdOrderByCreatedAtDesc(Long couponId, Long customerProfileId);

    long countByBusinessIdAndStatus(Long businessId, CustomerCouponStatus status);

    @Modifying
    @Query("UPDATE CustomerCoupon cc SET cc.status = com.shabanaj.beloyal.model.Enums.CustomerCouponStatus.EXPIRED " +
           "WHERE cc.status = com.shabanaj.beloyal.model.Enums.CustomerCouponStatus.REDEEMED " +
           "AND cc.expiresAt IS NOT NULL AND cc.expiresAt < :now")
    int expireOverdueCustomerCoupons(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM CustomerCoupon cc WHERE cc.business.id = :businessId")
    void deleteByBusinessId(@Param("businessId") Long businessId);

    @Modifying
    @Query("DELETE FROM CustomerCoupon cc WHERE cc.customerProfile.id = :customerProfileId")
    void deleteByCustomerProfileId(@Param("customerProfileId") Long customerProfileId);
}
