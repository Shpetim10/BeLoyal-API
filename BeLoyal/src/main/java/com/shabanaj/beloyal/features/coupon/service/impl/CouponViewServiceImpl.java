package com.shabanaj.beloyal.features.coupon.service.impl;

import com.shabanaj.beloyal.common.Exception.CouponNotFound;
import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.features.coupon.dto.CouponSummaryResponse;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.coupon.service.CouponViewService;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponViewServiceImpl implements CouponViewService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CouponSummaryResponse> listCoupons(Long businessId, CouponStatus status, CouponType type,
                                                    String search, int page, int limit,
                                                    String sortBy, String sortDirection) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        String resolvedSortBy = resolveSort(sortBy);
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(direction, resolvedSortBy));

        return couponRepository
                .findAllByBusiness(businessId, status, type,
                        (search != null && !search.isBlank()) ? search : null,
                        pageRequest)
                .map(couponMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponDetailResponse getCoupon(Long businessId, Long couponId) {
        LoyaltyCoupon coupon = couponRepository
                .findByIdAndBusinessIdAndDeletedAtIsNull(couponId, businessId)
                .orElseThrow(() -> new CouponNotFound(couponId));
        return couponMapper.toDetail(coupon);
    }

    private String resolveSort(String sortBy) {
        if (sortBy == null) return "createdAt";
        return switch (sortBy) {
            case "title" -> "title";
            case "pointsCost" -> "pointsCost";
            case "status" -> "status";
            case "startDate" -> "startDate";
            case "endDate" -> "endDate";
            default -> "createdAt";
        };
    }
}
