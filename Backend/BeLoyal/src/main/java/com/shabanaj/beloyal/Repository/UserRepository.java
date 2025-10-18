package com.shabanaj.beloyal.Repository;

import com.shabanaj.beloyal.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
