package com.shabanaj.beloyal.businessMember.repository;

import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessMemberRepository extends JpaRepository<BusinessMember, Long> {
    boolean existsByUser_IdAndBusiness_Id(Long userId, Long businessId);
    Optional<BusinessMember> findByUser_IdAndBusiness_Id(Long userId, Long businessId);
    Optional<BusinessMember> findByUser(User user);
    List<BusinessMember> findAllByBusiness_Id(Long businessId);
}
