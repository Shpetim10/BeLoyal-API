package com.shabanaj.beloyal.auth.service.impl;

import com.shabanaj.beloyal.auth.service.AdminBusinessRejectionService;
import com.shabanaj.beloyal.business.service.BusinessService;
import com.shabanaj.beloyal.email.service.EmailService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.businessMember.service.BusinessMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBusinessRejectionServiceImpl implements AdminBusinessRejectionService {
    private BusinessService businessService;
    private final EmailService emailService;
    private final Clock clock;
    private final BusinessMemberService businessMemberService;

    @Override
    public void rejectBusinessRegistration(Long businessId, Long adminId, String rejectReason) {
        Business business= businessService.getBusinessById(businessId);
        List<BusinessMember> memberList= businessMemberService.getBusinessMembersByBusiness(business);

        // Mark business and members as rejected and inactive
        business.reject(adminId, clock, rejectReason);
        memberList.forEach(BusinessMember::deactivate);

        // Send rejection email
        emailService.sendBusinessRejectionEmail(memberList, business, rejectReason);
    }
}
