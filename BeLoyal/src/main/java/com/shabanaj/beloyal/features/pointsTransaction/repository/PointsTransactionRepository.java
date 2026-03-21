package com.shabanaj.beloyal.features.pointsTransaction.repository;

import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointsTransactionRepository extends JpaRepository<PointsTransaction, Integer> {
    List<PointsTransaction> findAllByBillTransactionId(Long billTransactionId);

    // Query for points transactions details
    @Query(
            "SELECT pt FROM PointsTransaction pt " +
                    "JOIN FETCH pt.loyaltyAccount la " +
                    "JOIN FETCH la.customerProfile cp " +
                    "JOIN FETCH cp.user u " +
                    "JOIN FETCH la.business b " +
                    "JOIN FETCH pt.billTransaction bt " +
                    "JOIN FETCH pt.businessMember bm " +
                    "JOIN FETCH bm.user bmu " +
                    "WHERE pt.id= :id"
    )
    PointsTransaction getPointsTransactionById(@Param("id") Long id);

    // Query for business member and business id
    @Query(
            "SELECT pt FROM PointsTransaction pt JOIN FETCH pt.loyaltyAccount la " +
                    "WHERE pt.businessMember.id= ?1 AND la.business.id=?2"
    )
    List<PointsTransaction> findAllByBusinessMemberIdAndBusinessId(Long businessMemberId, Long businessId);

    // Query for business
    @Query("SELECT pt FROM PointsTransaction pt " +
            "JOIN FETCH pt.loyaltyAccount la " +
            "JOIN FETCH la.customerProfile cp " +
            "JOIN FETCH cp.user u " +
            "JOIN FETCH pt.businessMember bm " +
            "JOIN FETCH bm.user bmu " +
            "LEFT JOIN FETCH pt.billTransaction bt " +
            "WHERE la.business.id = :businessId " +
            "ORDER BY pt.createdAt DESC")
    List<PointsTransaction> findAllByBusinessId(@Param("businessId") Long businessId);

    // Query for customer by user id
    @Query(
            "SELECT pt FROM PointsTransaction pt JOIN FETCH pt.loyaltyAccount la " +
                    "WHERE la.customerProfile.user.id = ?1"
    )
    List<PointsTransaction> findAllByUserId(Long userId);

    // Query for customer by user id and business id
    @Query(
            "SELECT pt FROM PointsTransaction pt JOIN FETCH pt.loyaltyAccount la " +
                    "WHERE la.customerProfile.user.id= ?1 AND la.business.id=?2"
    )
    List<PointsTransaction> findAllByUserIdAndBusinessId(Long userId, Long businessId);
}
