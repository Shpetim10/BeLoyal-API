package com.shabanaj.beloyal.Repository;

import com.shabanaj.beloyal.Entity.BusinessMember;
import com.shabanaj.beloyal.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessMemberRepository extends JpaRepository<BusinessMember, Long> {
    Optional<BusinessMember> findByUser(User user);
}
