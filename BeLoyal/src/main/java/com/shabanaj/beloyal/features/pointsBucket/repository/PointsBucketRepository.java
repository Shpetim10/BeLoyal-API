package com.shabanaj.beloyal.features.pointsBucket.repository;

import com.shabanaj.beloyal.model.Entity.PointsBucket;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

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
}
