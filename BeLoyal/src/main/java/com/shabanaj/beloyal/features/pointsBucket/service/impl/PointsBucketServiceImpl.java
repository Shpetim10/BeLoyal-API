package com.shabanaj.beloyal.features.pointsBucket.service.impl;

import com.shabanaj.beloyal.common.Exception.InsufficientPointsException;
import com.shabanaj.beloyal.features.loyaltySettings.service.LoyaltySettingsService;
import com.shabanaj.beloyal.features.pointsBucket.repository.PointsBucketRepository;
import com.shabanaj.beloyal.features.pointsBucket.service.PointsBucketService;
import com.shabanaj.beloyal.features.pointsBucketConsumption.service.PointsBucketConsumptionService;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionService;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import com.shabanaj.beloyal.model.Entity.LoyaltySettings;
import com.shabanaj.beloyal.model.Entity.PointsBucket;
import com.shabanaj.beloyal.model.Entity.PointsBucketConsumption;
import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import com.shabanaj.beloyal.model.Enums.PointsBucketStatus;
import com.shabanaj.beloyal.model.Enums.PointsType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PointsBucketServiceImpl implements PointsBucketService {
    private final PointsBucketRepository pointsBucketRepository;
    private final LoyaltySettingsService loyaltySettingsService;
    private final PointsTransactionService pointsTransactionService;
    private final PointsBucketConsumptionService pointsBucketConsumptionService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Self-injection allows expireSingleBucket's @Transactional(REQUIRES_NEW) to be honoured
    @Lazy
    @Autowired
    private PointsBucketService self;

    @Override
    public PointsBucket save(PointsBucket pointsBucket) {
        return pointsBucketRepository.save(pointsBucket);
    }

    @Override
    public void expireBuckets() {
        List<Long> ids = pointsBucketRepository.findExpiredBucketIds();
        logger.info("ExpireBuckets: {} bucket(s) to expire", ids.size());
        int failed = 0;
        for (Long id : ids) {
            try {
                self.expireSingleBucket(id);
            } catch (Exception e) {
                failed++;
                logger.error("ExpireBuckets: failed to expire bucket {}: {}", id, e.getMessage());
            }
        }
        logger.info("ExpireBuckets: finished — {}/{} failed", failed, ids.size());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void expireSingleBucket(Long bucketId) {
        PointsBucket pointsBucket = pointsBucketRepository.findById(bucketId).orElse(null);
        if (pointsBucket == null || pointsBucket.getStatus() != PointsBucketStatus.ACTIVE) {
            return; // already expired by a concurrent run
        }

        LoyaltyAccount loyaltyAccount = pointsBucket.getLoyaltyAccount();
        loyaltyAccount.expire(pointsBucket.getPointsRemaining());

        pointsBucket.setStatus(PointsBucketStatus.EXPIRED);

        LoyaltySettings loyaltySettings = loyaltySettingsService.getLoyaltySettings(loyaltyAccount.getBusiness().getId());

        PointsTransaction pointsTransaction = PointsTransaction.builder()
                .loyaltyAccount(loyaltyAccount)
                .billTransaction(pointsBucket.getSourceTransaction() != null ? pointsBucket.getSourceTransaction().getBillTransaction() : null)
                .businessMember(null)
                .type(PointsType.EXPIRE)
                .description("Points expired. Created on " +
                        pointsBucket.getEarnedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        " and expired on " +
                        pointsBucket.getExpiresAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".")
                .reason("Points expire after " +
                        loyaltySettings.getMonthsToExpire() +
                        " months per business loyalty settings.")
                .pointsDelta(pointsBucket.getPointsRemaining() * (-1))
                .build();

        pointsTransactionService.save(pointsTransaction);
        pointsBucketRepository.save(pointsBucket);
    }

    @Override
    @Transactional
    public void spend(Long loyaltyAccountId, int pointsToSpend, PointsTransaction consumingTransaction) {
        if (pointsToSpend <= 0) return;

        List<PointsBucket> buckets = pointsBucketRepository.findSpendableBuckets(loyaltyAccountId);

        int remaining = pointsToSpend;
        for (PointsBucket bucket : buckets) {
            if (remaining <= 0) break;

            int consumed = Math.min(bucket.getPointsRemaining(), remaining);
            bucket.setPointsRemaining(bucket.getPointsRemaining() - consumed);

            if (bucket.getPointsRemaining() == 0) {
                bucket.setStatus(PointsBucketStatus.FULLY_USED);
            }

            pointsBucketRepository.save(bucket);

            PointsBucketConsumption consumption = new PointsBucketConsumption();
            consumption.setTransaction(consumingTransaction);
            consumption.setPointsBucket(bucket);
            consumption.setPointsUsed(consumed);
            pointsBucketConsumptionService.save(consumption);

            remaining -= consumed;
        }

        if (remaining > 0) {
            throw new InsufficientPointsException(pointsToSpend - remaining, pointsToSpend);
        }
    }
}
