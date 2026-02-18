package com.shabanaj.beloyal.Repository;

import com.shabanaj.beloyal.Entity.BusinessMember;
import com.shabanaj.beloyal.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessMemberRepository extends JpaRepository<BusinessMember, Long> {
    boolean existsByUser_IdAndBusiness_Id(Long userId, Long businessId);
    Optional<BusinessMember> findByUser_IdAndBusiness_Id(Long userId, Long businessId);
    Optional<BusinessMember> findByUser(User user);
    List<BusinessMember> findAllByBusiness_Id(Long businessId);
}
