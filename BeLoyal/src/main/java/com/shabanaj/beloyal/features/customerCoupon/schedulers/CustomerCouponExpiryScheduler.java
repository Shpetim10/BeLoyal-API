package com.shabanaj.beloyal.features.customerCoupon.schedulers;

import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomerCouponExpiryScheduler {

    private final CustomerCouponRepository customerCouponRepository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Scheduled(cron = "0 5 2 * * *")
    @Transactional
    public void expireOverdueCustomerCoupons() {
        LocalDateTime now = LocalDateTime.now();
        int count = customerCouponRepository.expireOverdueCustomerCoupons(now);
        if (count > 0) {
            logger.info("CustomerCouponExpiryScheduler: marked {} coupon(s) as EXPIRED", count);
        }
    }
}
