package com.shabanaj.beloyal.businessMember.service;

import com.shabanaj.beloyal.business.repository.BusinessRepository;
import com.shabanaj.beloyal.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.common.Exception.BusinessNotFound;
import com.shabanaj.beloyal.common.Exception.UserNotFound;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessMemberStatusServiceImpl implements BusinessMemberStatusServcie{
    private final BusinessMemberRepository businessMemberRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    @Override
    public void changeStatusAndSave(User user, Business business, BusinessMember.MemberStatus memberStatus) {
        if(user == null || business == null || memberStatus == null){
            throw new InvalidParameterException();
        }

        Optional<BusinessMember> businessMember= businessMemberRepository.findByUserAndBusiness(user, business);
        if(businessMember.isEmpty()){
            throw new UserNotFound();
        }

        BusinessMember bm= businessMember.get();
        bm.setMemberStatus(memberStatus);

        businessMemberRepository.save(bm);
    }

    @Override
    public void changeStatusAndSave(Long userId, Long businessId, BusinessMember.MemberStatus memberStatus) {
        if(userId==null ||  businessId==null || memberStatus==null){
            throw new InvalidParameterException();
        }

        Optional<User> u= userRepository.findById(userId);
        if(u.isEmpty()){
            throw new UserNotFound();
        }
        User user= u.get();

        Optional<Business> b= businessRepository.findById(businessId);
        if(b.isEmpty()){
            throw new BusinessNotFound();
        }
        Business business= b.get();

        changeStatusAndSave(user,business,memberStatus);
    }

    @Override
    public void changeStatusAndSave(Long memberId, BusinessMember.MemberStatus memberStatus) {
        if (memberId==null || memberStatus == null){
            throw new InvalidParameterException();
        }

        Optional<BusinessMember> bm= businessMemberRepository.findById(memberId);
        if(bm.isEmpty()){
            throw new UserNotFound();
        }

        BusinessMember businessMember= bm.get();
        businessMember.setMemberStatus(memberStatus);
        businessMemberRepository.save(businessMember);
    }
}
