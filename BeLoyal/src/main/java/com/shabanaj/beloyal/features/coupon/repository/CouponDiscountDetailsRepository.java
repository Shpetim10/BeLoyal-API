package com.shabanaj.beloyal.features.coupon.repository;

import com.shabanaj.beloyal.model.Entity.CouponDiscountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponDiscountDetailsRepository extends JpaRepository<CouponDiscountDetails, Long> {
    Optional<CouponDiscountDetails> findByCouponId(Long couponId);

    @Modifying
    @Query("DELETE FROM CouponDiscountDetails cd WHERE cd.coupon.business.id = :businessId")
    void deleteByBusinessId(@Param("businessId") Long businessId);
}
