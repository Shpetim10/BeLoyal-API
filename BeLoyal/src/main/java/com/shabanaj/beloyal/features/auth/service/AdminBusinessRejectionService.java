package com.shabanaj.beloyal.features.auth.service;

public interface AdminBusinessRejectionService {
    void rejectBusinessRegistration(Long businessId, Long adminId, String rejectReason);
}
