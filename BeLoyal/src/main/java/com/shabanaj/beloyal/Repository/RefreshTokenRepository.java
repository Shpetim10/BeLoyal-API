package com.shabanaj.beloyal.Repository;

import com.shabanaj.beloyal.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String token);
    List<RefreshToken> findAllByUserIdAndRevokedAtIsNull(Long userId);
}
