package com.shabanaj.beloyal.features.pointsBucket.repository;

import com.shabanaj.beloyal.model.Entity.PointsBucket;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

import static org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE;
import static org.hibernate.jpa.SpecHints.HINT_SPEC_LOCK_TIMEOUT;

public interface PointsBucketRepository extends JpaRepository<PointsBucket, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = HINT_FETCH_SIZE, value = "500"),
            @QueryHint(name = HINT_SPEC_LOCK_TIMEOUT, value = "-2"), // "-2" is SKIP LOCKED in many dialects
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")
    })
    @Query("SELECT pb FROM PointsBucket pb " +
            "JOIN FETCH pb.loyaltyAccount " +
            "WHERE pb.expiresAt < CURRENT_TIMESTAMP " +
            "AND pb.status = 'ACTIVE'")
    Stream<PointsBucket> streamExpiredPointsBuckets();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pb FROM PointsBucket pb " +
            "WHERE pb.loyaltyAccount.id = :loyaltyAccountId " +
            "AND pb.status = 'ACTIVE' " +
            "AND pb.pointsRemaining > 0 " +
            "AND (pb.expiresAt IS NULL OR pb.expiresAt >= CURRENT_TIMESTAMP) " +
            "ORDER BY CASE WHEN pb.expiresAt IS NULL THEN 1 ELSE 0 END ASC, pb.expiresAt ASC")
    List<PointsBucket> findSpendableBuckets(@Param("loyaltyAccountId") Long loyaltyAccountId);
}
