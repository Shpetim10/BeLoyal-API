package com.shabanaj.beloyal.features.pointsTransaction.repository;

import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

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
                    "JOIN FETCH la.business b " +
                    "JOIN FETCH la.customerProfile cp " +
                    "JOIN FETCH cp.user u " +
                    "JOIN FETCH pt.billTransaction bt " +
                    "JOIN FETCH pt.businessMember bm " +
                    "JOIN FETCH bm.user bmu " +
                    "WHERE bmu.id= ?1 AND la.business.id=?2 " +
                    "ORDER BY pt.createdAt DESC "
    )
    List<PointsTransaction> findAllByBusinessMemberUserIdAndBusinessId(Long userId, Long businessId);

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
            "SELECT pt FROM PointsTransaction pt " +
                    "JOIN FETCH pt.loyaltyAccount la "+
                    "JOIN FETCH la.customerProfile cp " +
                    "JOIN FETCH cp.user u " +
                    "JOIN FETCH pt.businessMember bm " +
                    "JOIN FETCH bm.user bmu " +
                    "LEFT JOIN FETCH pt.billTransaction bt " +
                    "WHERE u.id = :userId " +
                    "ORDER BY pt.createdAt DESC"
    )
    Stream<PointsTransaction> findAllByUserId(@Param("userId") Long userId);

    // Query for customer by user id and business id
    @Query(
            "SELECT pt FROM PointsTransaction pt " +
                    "JOIN FETCH pt.loyaltyAccount la " +
                    "JOIN FETCH la.customerProfile cp " +
                    "JOIN FETCH cp.user u " +
                    "JOIN FETCH la.business b " +
                    "JOIN FETCH pt.businessMember bm " +
                    "JOIN FETCH bm.user bmu " +
                    "LEFT JOIN FETCH pt.billTransaction bt " +
                    "WHERE u.id= :userId AND b.id= :busienssId "+
                    "ORDER BY pt.createdAt DESC"
    )
    Stream<PointsTransaction> findAllByUserIdAndBusinessId(@Param("userId") Long userId, @Param("businessId") Long businessId);

    // security check
    @Query("SELECT COUNT(pt) > 0 FROM PointsTransaction pt " +
            "WHERE pt.id = :transactionId " +
            "AND (pt.loyaltyAccount.customerProfile.user.id = :userId " + // Is the Customer
            "OR pt.businessMember.user.id = :userId " +                 // Is the specific BM
            "OR pt.loyaltyAccount.business.id IN " +                    // Has role in Business
            "    (SELECT bm.business.id FROM BusinessMember bm WHERE bm.user.id = :userId))")
    boolean hasAccess(@Param("transactionId") Long transactionId, @Param("userId") Long userId);
}
