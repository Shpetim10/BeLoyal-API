package com.shabanaj.beloyal.features.business.service.impl;

import com.shabanaj.beloyal.common.email.service.EmailService;
import com.shabanaj.beloyal.features.business.repository.BusinessRepository;
import com.shabanaj.beloyal.features.business.service.BusinessLifecycleService;
import com.shabanaj.beloyal.features.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessLifecycleServiceImpl implements BusinessLifecycleService {

    private final BusinessRepository businessRepository;
    private final BusinessMemberRepository businessMemberRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void suspendBusiness(Long businessId, String reason) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        business.setBusinessStatus(BusinessStatus.INACTIVE);
        businessRepository.save(business);

        List<BusinessMember> members = businessMemberRepository.findALlByBusinessId(businessId);
        if (!members.isEmpty()) {
            emailService.sendBusinessSuspendedEmail(members, business, reason);
        }
    }

    @Override
    @Transactional
    public void banBusiness(Long businessId, String reason) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        business.setBusinessStatus(BusinessStatus.BANNED);
        businessRepository.save(business);

        List<BusinessMember> members = businessMemberRepository.findALlByBusinessId(businessId);
        if (!members.isEmpty()) {
            emailService.sendBusinessBannedEmail(members, business, reason);
        }
    }

    @Override
    @Transactional
    public void reactivateBusiness(Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        business.setBusinessStatus(BusinessStatus.ACTIVE);
        businessRepository.save(business);

        List<BusinessMember> members = businessMemberRepository.findALlByBusinessId(businessId);
        if (!members.isEmpty()) {
            emailService.sendBusinessReactivatedEmail(members, business);
        }
    }
}
