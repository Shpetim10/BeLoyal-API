package com.shabanaj.beloyal.features.coupon.service;

import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.features.coupon.dto.CouponSummaryResponse;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import org.springframework.data.domain.Page;

public interface CouponViewService {
    Page<CouponSummaryResponse> listActiveCoupons(Long businessId, CouponStatus status, CouponType type,
                                            String search, int page, int limit,
                                            String sortBy, String sortDirection);

    Page<CouponSummaryResponse> listArchivedCoupons(Long businessId, CouponType type,
                                                    String search, int page, int limit,
                                                    String sortBy, String sortDirection);

    Page<CouponSummaryResponse> listExpiredCoupons(Long businessId, CouponType type,
                                                   String search, int page, int limit,
                                                   String sortBy, String sortDirection);

    Page<CouponSummaryResponse> listTrashedCoupons(Long businessId, CouponType type,
                                                   String search, int page, int limit,
                                                   String sortBy, String sortDirection);

    CouponDetailResponse getCoupon(Long businessId, Long couponId);
}
