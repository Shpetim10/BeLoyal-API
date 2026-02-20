package com.shabanaj.beloyal.token.service;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.StaffInviteToken;
import com.shabanaj.beloyal.model.Entity.User;

import java.time.LocalDate;

public interface StaffInviteTokenService {
    StaffInviteToken generateStaffInviteToken(User user, Business business, LocalDate hiredAt, boolean isExistingUser);
    StaffInviteToken getStaffInviteToken(String token);
    void markTokenAsUsed(String token);
}
