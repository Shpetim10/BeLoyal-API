package com.shabanaj.beloyal.features.userProfiles.business.service;

import com.shabanaj.beloyal.features.userProfiles.business.dto.BusinessProfileDto;

public interface BusinessProfileService {
    BusinessProfileDto getBusinessProfile(Long businessId);
}
