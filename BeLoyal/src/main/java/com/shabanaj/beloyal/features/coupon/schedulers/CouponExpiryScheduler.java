package com.shabanaj.beloyal.features.coupon.schedulers;

import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponExpiryScheduler {

    private final CouponRepository couponRepository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireOverdueCoupons() {
        LocalDateTime now = LocalDateTime.now();
        List<LoyaltyCoupon> expired = couponRepository.findActiveExpiredCoupons(now);

        if (expired.isEmpty()) return;

        logger.info("CouponExpiryScheduler: expiring {} coupon(s)", expired.size());

        for (LoyaltyCoupon coupon : expired) {
            coupon.setStatus(CouponStatus.EXPIRED);
        }

        couponRepository.saveAll(expired);
        logger.info("CouponExpiryScheduler: finished");
    }
}
