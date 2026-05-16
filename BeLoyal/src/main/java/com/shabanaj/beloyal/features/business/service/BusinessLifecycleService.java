package com.shabanaj.beloyal.features.business.service;

import com.shabanaj.beloyal.model.Entity.Business;

public interface BusinessLifecycleService {
    void suspendBusiness(Long businessId, String reason);
    void banBusiness(Long businessId, String reason);
    void reactivateBusiness(Long businessId);
}
