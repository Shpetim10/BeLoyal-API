package com.shabanaj.beloyal.features.userProfiles.business.service;

import com.shabanaj.beloyal.features.userProfiles.business.dto.BusinessProfileUpdateDto;

public interface BusinessProfileUpdateService {
    void updateBusinessProfile(BusinessProfileUpdateDto dto, Long businessId);
}
