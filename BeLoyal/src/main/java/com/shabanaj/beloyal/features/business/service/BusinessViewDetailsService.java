package com.shabanaj.beloyal.features.business.service;

import com.shabanaj.beloyal.features.business.dto.BusinessDetailsDto;
import com.shabanaj.beloyal.features.businessMember.dto.BusinessListView;

import java.util.List;

public interface BusinessViewDetailsService {
    List<BusinessListView> getBusinesses();
    BusinessDetailsDto getBusinessDetails(Long businessId);
}
