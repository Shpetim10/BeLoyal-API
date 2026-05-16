package com.shabanaj.beloyal.features.token.repository;

import com.shabanaj.beloyal.model.Entity.ResetPasswordToken;
import com.shabanaj.beloyal.model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken,String> {
    Optional<ResetPasswordToken> findByToken(String token);
    List<ResetPasswordToken> findByUser(User user);

    @Modifying
    @Query("DELETE FROM ResetPasswordToken t WHERE t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
