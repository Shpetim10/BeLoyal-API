package com.shabanaj.beloyal.features.registration.service;

public interface AdminBusinessRejectionService {
    void rejectBusinessRegistration(Long businessId, Long adminId, String rejectReason);
}
