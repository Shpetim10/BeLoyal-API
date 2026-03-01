package com.shabanaj.beloyal.features.auth.service.impl;

import com.shabanaj.beloyal.features.auth.service.AdminBusinessRejectionService;
import com.shabanaj.beloyal.features.business.repository.BusinessRepository;
import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.common.email.service.EmailService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberService;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBusinessRejectionServiceImpl implements AdminBusinessRejectionService {
    private final BusinessService businessService;
    private final EmailService emailService;
    private final Clock clock;
    private final BusinessMemberService businessMemberService;
    private final BusinessRepository businessRepository;
    private final BusinessMemberRepository businessMemberRepository;

    @Override
    @Transactional
    public void rejectBusinessRegistration(Long businessId, Long adminId, String rejectReason) {
        Business business = businessService.getBusinessById(businessId);
        List<BusinessMember> memberList = businessMemberService.getBusinessMembersByBusiness(business);

        // Mark business and members as rejected and inactive
        business.reject(adminId, clock, rejectReason);
        businessService.updateBusiness(business);

        memberList.forEach(member -> {
            member.deactivate();
            businessMemberService.save(member);
        });

        // Send rejection email
        emailService.sendBusinessRejectionEmail(memberList, business, rejectReason);
    }
}
