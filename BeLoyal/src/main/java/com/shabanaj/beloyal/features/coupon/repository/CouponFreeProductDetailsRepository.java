package com.shabanaj.beloyal.features.coupon.repository;

import com.shabanaj.beloyal.model.Entity.CouponFreeProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponFreeProductDetailsRepository extends JpaRepository<CouponFreeProductDetails, Long> {
    Optional<CouponFreeProductDetails> findByCouponId(Long couponId);
}
