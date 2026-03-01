package com.shabanaj.beloyal.common.token.repository;

import com.shabanaj.beloyal.model.Entity.StaffInviteToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffInviteTokenRepository extends JpaRepository<StaffInviteToken,Long> {
    Optional<StaffInviteToken> findByToken(String token);
}
