package com.shabanaj.beloyal.user.repository;

import com.shabanaj.beloyal.model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByPhoneNumber(String phoneNumber);
    Optional<User> findUserByEmailIgnoreCase(String email);
}
