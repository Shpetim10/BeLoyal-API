package com.shabanaj.beloyal.features.coupon.repository;

import com.shabanaj.beloyal.model.Entity.CouponFreeProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponFreeProductDetailsRepository extends JpaRepository<CouponFreeProductDetails, Long> {
    Optional<CouponFreeProductDetails> findByCouponId(Long couponId);

    @Modifying
    @Query("DELETE FROM CouponFreeProductDetails cfd WHERE cfd.coupon.business.id = :businessId")
    void deleteByBusinessId(@Param("businessId") Long businessId);
}
