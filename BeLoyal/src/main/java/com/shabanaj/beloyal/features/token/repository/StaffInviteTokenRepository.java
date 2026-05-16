package com.shabanaj.beloyal.features.token.repository;

import com.shabanaj.beloyal.model.Entity.StaffInviteToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StaffInviteTokenRepository extends JpaRepository<StaffInviteToken,Long> {
    Optional<StaffInviteToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM StaffInviteToken sit WHERE sit.business.id = :businessId")
    void deleteByBusinessId(@Param("businessId") Long businessId);

    @Modifying
    @Query("DELETE FROM StaffInviteToken sit WHERE sit.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
