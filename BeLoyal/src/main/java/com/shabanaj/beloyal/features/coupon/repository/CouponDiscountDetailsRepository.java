package com.shabanaj.beloyal.features.coupon.repository;

import com.shabanaj.beloyal.model.Entity.CouponDiscountDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponDiscountDetailsRepository extends JpaRepository<CouponDiscountDetails, Long> {
    Optional<CouponDiscountDetails> findByCouponId(Long couponId);
}
