package com.shabanaj.beloyal.features.coupon.repository;

import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CouponVisibility;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<LoyaltyCoupon, Long> {

    Optional<LoyaltyCoupon> findByIdAndBusinessIdAndDeletedAtIsNull(Long id, Long businessId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoyaltyCoupon> findWithLockByIdAndDeletedAtIsNull(Long id);

    @Query("""
            SELECT c FROM LoyaltyCoupon c
            WHERE c.business.id = :businessId
              AND c.deletedAt IS NULL
              AND c.status NOT IN (com.shabanaj.beloyal.model.Enums.CouponStatus.ARCHIVED, com.shabanaj.beloyal.model.Enums.CouponStatus.EXPIRED)
              AND (:status IS NULL OR c.status = :status)
              AND (:type IS NULL OR c.type = :type)
              AND (:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<LoyaltyCoupon> findAllByBusiness(
            @Param("businessId") Long businessId,
            @Param("status") CouponStatus status,
            @Param("type") CouponType type,
            @Param("search") String search,
            Pageable pageable);

    @Query("""
            SELECT c FROM LoyaltyCoupon c
            WHERE c.business.id = :businessId
              AND c.deletedAt IS NULL
              AND c.status = com.shabanaj.beloyal.model.Enums.CouponStatus.ARCHIVED
              AND (:type IS NULL OR c.type = :type)
              AND (:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<LoyaltyCoupon> findArchivedByBusiness(
            @Param("businessId") Long businessId,
            @Param("type") CouponType type,
            @Param("search") String search,
            Pageable pageable);

    @Query("""
            SELECT c FROM LoyaltyCoupon c
            WHERE c.business.id = :businessId
              AND c.deletedAt IS NULL
              AND c.status = com.shabanaj.beloyal.model.Enums.CouponStatus.EXPIRED
              AND (:type IS NULL OR c.type = :type)
              AND (:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<LoyaltyCoupon> findExpiredByBusiness(
            @Param("businessId") Long businessId,
            @Param("type") CouponType type,
            @Param("search") String search,
            Pageable pageable);

    @Query("""
            SELECT c FROM LoyaltyCoupon c
            WHERE c.business.id = :businessId
              AND c.deletedAt IS NOT NULL
              AND (:type IS NULL OR c.type = :type)
              AND (:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<LoyaltyCoupon> findTrashedByBusiness(
            @Param("businessId") Long businessId,
            @Param("type") CouponType type,
            @Param("search") String search,
            Pageable pageable);

    Optional<LoyaltyCoupon> findByIdAndBusinessIdAndDeletedAtIsNotNull(Long id, Long businessId);
    Optional<LoyaltyCoupon> findByIdAndBusinessId(Long id, Long businessId);
    @Query("""
            SELECT c FROM LoyaltyCoupon c
            WHERE c.business.id = :businessId
              AND c.deletedAt IS NULL
              AND c.status = 'ACTIVE'
              AND c.visibility = 'PUBLIC'
              AND c.startDate <= :now
              AND c.endDate >= :now
              AND (c.totalRedemptionLimit IS NULL OR c.totalRedemptions < c.totalRedemptionLimit)
            ORDER BY c.isFeatured DESC, c.sortOrder ASC, c.createdAt DESC
            """)
    List<LoyaltyCoupon> findAvailableForBusiness(
            @Param("businessId") Long businessId,
            @Param("now") LocalDateTime now);

    @Query("""
            SELECT c FROM LoyaltyCoupon c
            JOIN FETCH c.business b
            WHERE c.deletedAt IS NULL
              AND c.status = com.shabanaj.beloyal.model.Enums.CouponStatus.ACTIVE
              AND c.visibility = com.shabanaj.beloyal.model.Enums.CouponVisibility.PUBLIC
              AND c.startDate <= :now
              AND c.endDate >= :now
              AND (c.totalRedemptionLimit IS NULL OR c.totalRedemptions < c.totalRedemptionLimit)
            ORDER BY c.isFeatured DESC, c.sortOrder ASC, c.createdAt DESC
            """)
    List<LoyaltyCoupon> findAllActivePublic(@Param("now") LocalDateTime now);

    int countByBusinessIdAndDeletedAtIsNull(Long businessId);

    @Query("SELECT c FROM LoyaltyCoupon c WHERE c.status = 'ACTIVE' AND c.endDate < :now AND c.deletedAt IS NULL")
    List<LoyaltyCoupon> findActiveExpiredCoupons(@Param("now") LocalDateTime now);

    @Query("""
            SELECT c FROM LoyaltyCoupon c
            WHERE c.business.id = :businessId
              AND c.deletedAt IS NULL
              AND c.status = 'ACTIVE'
              AND c.visibility = 'PUBLIC'
              AND c.startDate <= :now
              AND c.endDate >= :now
              AND (c.totalRedemptionLimit IS NULL OR c.totalRedemptions < c.totalRedemptionLimit)
            ORDER BY  c.pointsCost ASC
            LIMIT 1
            """)
    Optional<LoyaltyCoupon> findCheapestCouponByBusinessId(@Param("businessId") Long businessId, @Param("now") LocalDateTime now);
}
