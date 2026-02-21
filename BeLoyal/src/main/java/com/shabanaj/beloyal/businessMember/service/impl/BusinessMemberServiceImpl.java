package com.shabanaj.beloyal.businessMember.service.impl;

import com.shabanaj.beloyal.common.Exception.UserNotFound;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.businessMember.service.BusinessMemberService;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessMemberServiceImpl implements BusinessMemberService {
    private final BusinessMemberRepository businessMemberRepository;

    public BusinessMemberServiceImpl(BusinessMemberRepository businessMemberRepository) {
        this.businessMemberRepository = businessMemberRepository;
    }

    @Override
    public BusinessMember createBusinessMember(User user, Business business, Role role, LocalDate date) {
        if(user==null || business==null || role==null){
            throw new InvalidParameterException("There are some missing parameters");
        }

        BusinessMember businessMember=new BusinessMember();
        businessMember.setBusiness(business);
        businessMember.setUser(user);
        businessMember.setRole(role);
        businessMember.setHiredAt(date);
        return businessMemberRepository.save(businessMember);
    }

    @Override
    public List<BusinessMember> getBusinessMembersByBusiness(Business business) {
        return businessMemberRepository.findAllByBusiness(business);
    }

    @Override
    public List<BusinessMember> getBusinessMembersByBusinessIdAndRole(Long businessId, Role role) {
        return businessMemberRepository.findAllByBusinessIdAndRole(businessId, role);
    }

    @Override
    public BusinessMember getBusinessMemberByUserAndBusiness(User user, Business business) {
        Optional<BusinessMember> businessMember=businessMemberRepository.findByUserAndBusiness(user, business);

        if(businessMember.isEmpty()){
            throw new UserNotFound();
        }
        return businessMember.get();
    }

    @Override
    public void save(BusinessMember businessMember) {
        businessMemberRepository.save(businessMember);
    }

    @Override
    public void changeStatusAndSave(User user, Business business, BusinessMember.MemberStatus status) {
        BusinessMember businessMember=getBusinessMemberByUserAndBusiness(user,business);
        businessMember.setMemberStatus(status);
        save(businessMember);
    }
}
