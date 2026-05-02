package com.shabanaj.beloyal.features.customerCoupon.repository;

import com.shabanaj.beloyal.model.Entity.CustomerCoupon;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerCouponRepository extends JpaRepository<CustomerCoupon, Long> {

    boolean existsByCouponId(Long couponId);

    int countByCouponIdAndCustomerProfileId(Long couponId, Long customerProfileId);

    List<CustomerCoupon> findAllByCustomerProfileIdOrderByCreatedAtDesc(Long customerProfileId);

    List<CustomerCoupon> findAllByCustomerProfileIdAndBusinessIdOrderByCreatedAtDesc(Long customerProfileId, Long businessId);

    Optional<CustomerCoupon> findByIdAndCustomerProfileId(Long id, Long customerProfileId);

    Optional<CustomerCoupon> findByIdAndBusinessId(Long id, Long businessId);
}
