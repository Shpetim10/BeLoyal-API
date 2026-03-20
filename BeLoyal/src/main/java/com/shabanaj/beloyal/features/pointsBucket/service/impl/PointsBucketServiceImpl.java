package com.shabanaj.beloyal.features.pointsBucket.service.impl;

import com.shabanaj.beloyal.features.loyaltyAccount.service.LoyaltyAccountService;
import com.shabanaj.beloyal.features.pointsBucket.repository.PointsBucketRepository;
import com.shabanaj.beloyal.features.pointsBucket.service.PointsBucketService;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import com.shabanaj.beloyal.model.Entity.PointsBucket;
import com.shabanaj.beloyal.model.Enums.PointsBucketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PointsBucketServiceImpl implements PointsBucketService {
    private final PointsBucketRepository pointsBucketRepository;
    private final LoyaltyAccountService loyaltyAccountService;

    @Override
    public PointsBucket save(PointsBucket pointsBucket) {
        return pointsBucketRepository.save(pointsBucket);
    }

    @Override
    @Transactional
    public void expireBuckets() {
        // Find all
        try(Stream<PointsBucket> pointsBuckets = pointsBucketRepository.streamExpiredPointsBuckets()){
            pointsBuckets.forEach(pointsBucket -> {
                // update loyalty account points
                LoyaltyAccount loyaltyAccount = pointsBucket.getLoyaltyAccount();
                loyaltyAccount.expire(pointsBucket.getPointsRemaining());

                // mark points bucket as expired
                pointsBucket.setStatus(PointsBucketStatus.EXPIRED);
            });
        }
    }
}
