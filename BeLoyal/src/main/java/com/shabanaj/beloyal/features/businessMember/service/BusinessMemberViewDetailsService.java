package com.shabanaj.beloyal.features.businessMember.service;

import com.shabanaj.beloyal.features.businessMember.dto.BusinessMemberDetailsDto;

import java.util.List;

public interface BusinessMemberViewDetailsService {
    List<BusinessMemberDetailsDto> getBusinessMemberDetails(Long businessId);
}
