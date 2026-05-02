package com.shabanaj.beloyal.features.coupon.service.impl;

import com.shabanaj.beloyal.common.Exception.CouponNotFound;
import com.shabanaj.beloyal.common.Exception.InvalidCouponOperationException;
import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.coupon.service.CouponLifecycleService;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CouponLifecycleServiceImpl implements CouponLifecycleService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    private static final Map<CouponStatus, Set<CouponStatus>> ALLOWED_TRANSITIONS = Map.of(
            CouponStatus.DRAFT, Set.of(CouponStatus.ACTIVE, CouponStatus.ARCHIVED),
            CouponStatus.ACTIVE, Set.of(CouponStatus.PAUSED, CouponStatus.EXPIRED, CouponStatus.ARCHIVED),
            CouponStatus.PAUSED, Set.of(CouponStatus.ACTIVE, CouponStatus.ARCHIVED),
            CouponStatus.EXPIRED, Set.of(CouponStatus.ARCHIVED),
            CouponStatus.ARCHIVED, Set.of()
    );

    @Override
    @Transactional
    public CouponDetailResponse changeStatus(Long businessId, Long couponId, CouponStatus newStatus) {
        LoyaltyCoupon coupon = couponRepository
                .findByIdAndBusinessIdAndDeletedAtIsNull(couponId, businessId)
                .orElseThrow(() -> new CouponNotFound(couponId));

        Set<CouponStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(coupon.getStatus(), Set.of());
        if (!allowed.contains(newStatus)) {
            throw new InvalidCouponOperationException(
                    "Cannot transition coupon from " + coupon.getStatus() + " to " + newStatus);
        }

        if (newStatus == CouponStatus.ACTIVE && coupon.getEndDate().isBefore(LocalDateTime.now())) {
            throw new InvalidCouponOperationException("Cannot activate a coupon with an expired end date");
        }

        if (newStatus == CouponStatus.ARCHIVED) {
            coupon.setArchivedAt(LocalDateTime.now());
        }

        coupon.setStatus(newStatus);
        coupon = couponRepository.save(coupon);
        return couponMapper.toDetail(coupon);
    }

    @Override
    @Transactional
    public void archive(Long businessId, Long couponId) {
        LoyaltyCoupon coupon = couponRepository
                .findByIdAndBusinessIdAndDeletedAtIsNull(couponId, businessId)
                .orElseThrow(() -> new CouponNotFound(couponId));

        coupon.setStatus(CouponStatus.ARCHIVED);
        coupon.setArchivedAt(LocalDateTime.now());
        couponRepository.save(coupon);
    }

    @Override
    @Transactional
    public void delete(Long businessId, Long couponId) {
        LoyaltyCoupon coupon = couponRepository
                .findByIdAndBusinessIdAndDeletedAtIsNull(couponId, businessId)
                .orElseThrow(() -> new CouponNotFound(couponId));

        coupon.setDeletedAt(LocalDateTime.now());
        couponRepository.save(coupon);
    }
}
