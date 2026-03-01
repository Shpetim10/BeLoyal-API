package com.shabanaj.beloyal.common.token.repository;

import com.shabanaj.beloyal.model.Entity.RefreshToken;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String token);
    List<RefreshToken> findAllByUserIdAndRevokedAtIsNull(Long userId);
    List<RefreshToken>  findAllByRevokedAtIsNotNull();
    void deleteAllByRevokedAtIsNotNull();
    void deleteAllByRevokedAtIsNotNullOrExpiresAtBefore(LocalDateTime expiredAt);
    @Modifying
    @Query("""
  delete from RefreshToken t
  where (t.revokedAt is not null and t.revokedAt < :revokedCutoff)
     or (t.expiresAt < :expiredCutoff)
""")
    int deleteOldTokens(@Param("revokedCutoff") LocalDateTime revokedCutoff,
                        @Param("expiredCutoff")  LocalDateTime expiredCutoff);
}
