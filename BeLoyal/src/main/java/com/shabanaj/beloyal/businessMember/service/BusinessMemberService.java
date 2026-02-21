package com.shabanaj.beloyal.businessMember.service;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;

import java.time.LocalDate;
import java.util.List;

public interface BusinessMemberService {
    BusinessMember createBusinessMember(User user, Business business, Role role, LocalDate date);
    List<BusinessMember> getBusinessMembersByBusiness(Business business);
    List<BusinessMember> getBusinessMembersByBusinessIdAndRole(Long businessId, Role role);
    BusinessMember getBusinessMemberByUserAndBusiness(User user, Business business);
    void save(BusinessMember businessMember);
    void changeStatusAndSave(User user, Business business, BusinessMember.MemberStatus status);
}
