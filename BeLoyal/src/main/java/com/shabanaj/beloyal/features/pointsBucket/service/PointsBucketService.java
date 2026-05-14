package com.shabanaj.beloyal.features.pointsBucket.service;

import com.shabanaj.beloyal.model.Entity.PointsBucket;
import com.shabanaj.beloyal.model.Entity.PointsTransaction;

public interface PointsBucketService {
    PointsBucket save(PointsBucket pointsBucket);
    void expireBuckets();

    /**
     * Drains {@code pointsToSpend} from the caller's ACTIVE, non-expired buckets
     * in FIFO order (earliest-expiring first). Creates a {@link com.shabanaj.beloyal.model.Entity.PointsBucketConsumption}
     * record per bucket touched and marks fully-drained buckets as {@code FULLY_USED}.
     *
     * Must be called inside an existing transaction so all writes are atomic.
     */
    void spend(Long loyaltyAccountId, int pointsToSpend, PointsTransaction consumingTransaction);
}
