package com.shabanaj.beloyal.token.repository;

import com.shabanaj.beloyal.model.Entity.ResetPasswordToken;
import com.shabanaj.beloyal.model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken,String> {
    Optional<ResetPasswordToken> findByToken(String token);
    List<ResetPasswordToken> findByUser(User user);
}
