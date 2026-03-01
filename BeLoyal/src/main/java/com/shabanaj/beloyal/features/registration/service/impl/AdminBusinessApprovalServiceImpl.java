package com.shabanaj.beloyal.features.registration.service.impl;

import com.shabanaj.beloyal.features.business.repository.BusinessRepository;
import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.common.email.service.EmailService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.features.registration.service.AdminBusinessApprovalService;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBusinessApprovalServiceImpl implements AdminBusinessApprovalService {
    private final BusinessService businessService;
    private final EmailService emailService;
    private final Clock clock;
    private final BusinessMemberService businessMemberService;
    private final BusinessRepository businessRepository;

    @Override
    @Transactional
    public void approveBusinessRegistration(Long businessId, Long adminId) {
        Business business = businessService.getBusinessById(businessId);
        List<BusinessMember> memberList = businessMemberService.getBusinessMembersByBusiness(business);

        // activate business and members and send mails
        business.activate(adminId, clock);
        businessRepository.save(business);
        memberList.forEach(member -> {
            member.activate();
            businessMemberService.save(member);
        });

        // send mails
        emailService.sendBusinessActivationEmail(memberList, business);
    }
}
