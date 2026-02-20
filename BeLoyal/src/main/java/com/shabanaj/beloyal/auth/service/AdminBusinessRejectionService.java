package com.shabanaj.beloyal.auth.service;

public interface AdminBusinessRejectionService {
    void rejectBusinessRegistration(Long businessId, Long adminId, String rejectReason);
}
