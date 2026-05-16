package com.shabanaj.beloyal.features.businessMember.repository;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BusinessMemberRepository extends JpaRepository<BusinessMember, Long> {
    boolean existsByUser_IdAndBusiness_Id(Long userId, Long businessId);
    Optional<BusinessMember> findByUserAndBusiness(User user, Business business);
    List<BusinessMember> findByUser(User user);
    List<BusinessMember> findByUserId(Long userId);
    List<BusinessMember> findAllByBusiness(Business business);
    List<BusinessMember> findAllByBusinessIdAndRole(Long businessId, Role role);
    boolean existsByBusinessIdAndUserIdAndRoleIn(Long businessId, Long userId, List<String> roles);
    Optional<BusinessMember> findByUserIdAndBusinessId(Long  userId, Long businessId);

    long countByBusinessIdAndRole(Long businessId, Role role);

    @Query("SELECT bm.user.id FROM BusinessMember bm WHERE bm.business.id = :businessId")
    List<Long> findUserIdsByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT COUNT(bm) FROM BusinessMember bm WHERE bm.user.id = :userId AND bm.business.id <> :excludeBusinessId")
    long countOtherMembershipsForUser(@Param("userId") Long userId, @Param("excludeBusinessId") Long excludeBusinessId);

    @Modifying
    @Query("DELETE FROM BusinessMember bm WHERE bm.business.id = :businessId")
    void deleteByBusinessId(@Param("businessId") Long businessId);

    @Modifying
    @Query("DELETE FROM BusinessMember bm WHERE bm.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // find by business id
    @Query(
            "SELECT bm FROM BusinessMember bm " +
                    "JOIN FETCH bm.user bmu " +
                    "JOIN FETCH bm.business bmb " +
                    "WHERE bmb.id= :businessId"
    )
    List<BusinessMember> findALlByBusinessId(@Param("businessId") Long businessId);
}
