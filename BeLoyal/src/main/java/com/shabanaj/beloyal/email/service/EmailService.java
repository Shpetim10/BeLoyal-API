package com.shabanaj.beloyal.email.service;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.StaffInviteToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;

import java.util.List;

public interface EmailService {
    void sendActivationEmail(User user, String token);
    void sendBusinessRegistrationEmail(User user, Business business);
    void sendBusinessActivationEmail(List<BusinessMember> members, Business business);
    void sendBusinessRejectionEmail(List<BusinessMember> members, Business business, String rejectReason);
    void sendStaffInvitationEmail(StaffInviteToken token, Business business, Role role);
}
