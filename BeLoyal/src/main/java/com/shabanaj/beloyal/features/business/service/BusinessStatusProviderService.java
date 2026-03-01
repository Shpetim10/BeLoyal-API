package com.shabanaj.beloyal.features.business.service;

import com.shabanaj.beloyal.model.Enums.BusinessStatus;

public interface BusinessStatusProviderService {
    BusinessStatus getBusinessStatus(Long businessId);
}
