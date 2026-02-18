package com.shabanaj.beloyal.token.repository;

import com.shabanaj.beloyal.model.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String token);
    List<RefreshToken> findAllByUserIdAndRevokedAtIsNull(Long userId);
}
