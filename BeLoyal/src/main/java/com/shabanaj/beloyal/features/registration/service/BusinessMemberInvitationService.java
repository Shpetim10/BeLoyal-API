package com.shabanaj.beloyal.features.registration.service;

import com.shabanaj.beloyal.features.registration.dto.businessMemberRegistration.BusinessMemberInviteDto;

public interface BusinessMemberInvitationService {
    void invite(BusinessMemberInviteDto dto, Long businessId);
}
