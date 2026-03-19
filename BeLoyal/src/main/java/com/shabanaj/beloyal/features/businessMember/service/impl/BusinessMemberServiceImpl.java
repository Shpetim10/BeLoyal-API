package com.shabanaj.beloyal.features.businessMember.service.impl;

import com.shabanaj.beloyal.common.Exception.BusinessMemberProfileNotFound;
import com.shabanaj.beloyal.common.Exception.UserNotFound;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.features.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessMemberServiceImpl implements BusinessMemberService {
    private final BusinessMemberRepository businessMemberRepository;
    private final Logger logger = LoggerFactory.getLogger(BusinessMemberServiceImpl.class);

    public BusinessMemberServiceImpl(BusinessMemberRepository businessMemberRepository) {
        this.businessMemberRepository = businessMemberRepository;
    }

    @Override
    public BusinessMember createBusinessMember(User user, Business business, Role role, LocalDate date) {
        if(user==null || business==null || role==null){
            throw new InvalidParameterException("There are some missing parameters");
        }
        logger.info("inside createBusinessMember");
        BusinessMember businessMember=new BusinessMember();
        businessMember.setBusiness(business);
        businessMember.setUser(user);
        businessMember.setRole(role);
        businessMember.setHiredAt(date);
        logger.info("business member values set");
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
    public BusinessMember getBusinessMemberByUserIdAndBusinessId(Long userId, Long businessId) {
        Optional<BusinessMember> businessMember= businessMemberRepository.findByUserIdAndBusinessId(userId, businessId);

        if(businessMember.isEmpty()){
            throw new BusinessMemberProfileNotFound("Business Member profile was not found");
        }

        if(!businessMember.get().getMemberStatus().equals(BusinessMember.MemberStatus.ACTIVE)){
            throw new AccessDeniedException("Access denied");
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
