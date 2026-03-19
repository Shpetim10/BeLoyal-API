package com.shabanaj.beloyal.features.businessMember.repository;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessMemberRepository extends JpaRepository<BusinessMember, Long> {
    boolean existsByUser_IdAndBusiness_Id(Long userId, Long businessId);
    Optional<BusinessMember> findByUserAndBusiness(User user, Business business);
    List<BusinessMember> findByUser(User user);
    List<BusinessMember> findAllByBusiness(Business business);
    List<BusinessMember> findAllByBusinessIdAndRole(Long businessId, Role role);
    boolean existsByBusinessIdAndUserIdAndRoleIn(Long businessId, Long userId, List<String> roles);
    Optional<BusinessMember> findByUserIdAndBusinessId(Long  userId, Long businessId);
}
