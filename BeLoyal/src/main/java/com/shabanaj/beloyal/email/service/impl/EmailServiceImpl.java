package com.shabanaj.beloyal.email.service.impl;

import com.shabanaj.beloyal.email.dto.EmailHtmlTemplates;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.StaffInviteToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.email.dto.SendEmailEvent;
import com.shabanaj.beloyal.email.service.EmailService;
import com.shabanaj.beloyal.model.Enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    private final ApplicationEventPublisher publisher;
    private final EmailHtmlTemplates emailHtmlTemplates;

    @Value("${app.activation.base-url}")
    private String activationBaseUrl;

    @Value("${app.staff.accept-invitation.base-url}")
    private String staffRegistrationBaseUrl;

    @Value("${app.password.forget-email.base-url}")
    private String passwordForgetEmailUrl;

    public EmailServiceImpl(ApplicationEventPublisher publisher, EmailHtmlTemplates emailHtmlTemplates) {
        this.publisher = publisher;
        this.emailHtmlTemplates = emailHtmlTemplates;
    }

    @Override
    public void sendActivationEmail(User user, String token) {
        String activationLink = activationBaseUrl + "?token=" + token;

        String subject = "Activate your account";
        String content = emailHtmlTemplates.buildActivationEmailHtml(user.getFirstName(), activationLink);

        sendMail(user.getEmail(), subject, content);
    }

    @Override
    public void sendBusinessRegistrationEmail(User user, Business business) {
        String subject = "✅ We received your business application";
        String content = emailHtmlTemplates.buildRegistrationEmailHtml(user, business);

        if(!user.getEmail().equals(business.getBusinessEmail())){
            sendMail(user.getEmail(), subject, content);
            sendMail(business.getBusinessEmail(), subject, content);
        }else{
            sendMail(user.getEmail(), subject, content);
        }
    }

    @Override
    public void sendBusinessActivationEmail(List<BusinessMember> members, Business business) {
        String subject= "✅ Your application was approved";

        members.forEach(member -> {
            if(!member.getUser().getEmail().equals(business.getBusinessEmail())){
                String content= emailHtmlTemplates.buildBusinessApprovalEmailHtml(member.getUser(), business);
                sendMail(member.getUser().getEmail(), subject, content);
            }
        });

        String content= emailHtmlTemplates.buildBusinessApprovalEmailHtml(members.get(0).getUser(), business);
        sendMail(business.getBusinessEmail(), subject, content);
    }

    @Override
    public void sendBusinessRejectionEmail(List<BusinessMember> members, Business business, String rejectionReason) {
        String subject= "❌ Your application was rejected";

        members.forEach(member -> {
            if(!member.getUser().getEmail().equals(business.getBusinessEmail())){
                String content= emailHtmlTemplates.buildBusinessRejectionEmailHtml(member.getUser(), business, rejectionReason);
                sendMail(member.getUser().getEmail(), subject, content);
            }
        });

        String content= emailHtmlTemplates.buildBusinessRejectionEmailHtml(members.get(0).getUser(), business, rejectionReason);
        sendMail(business.getBusinessEmail(), subject, content);
    }

    @Override
    public void sendStaffInvitationEmail(StaffInviteToken token, Business business, Role role){
        String inviteUrl = staffRegistrationBaseUrl + "?token=" + token.getToken()+"&existing="+token.isExistingUser()+"&email="+token.getUser().getEmail();

        String subject = "Invitation for "+business.getBusinessName()+ " staff";
        String content = emailHtmlTemplates.buildStaffInvitationEmailHtml(business, role.name(), inviteUrl, inviteUrl);

        sendMail(token.getUser().getEmail(), subject, content);
    }

    @Override
    public void sendForgetPasswordEmail(User user, String token) {
        String forgetPasswordUrl= passwordForgetEmailUrl + "?token=" + token;

        String subject= "Reset your password";
        String content = emailHtmlTemplates.buildForgetPasswordEmailHtml(user, forgetPasswordUrl);

        sendMail(user.getEmail(), subject, content);
    }

    // Helpers
    private void sendMail(String to, String subject, String body) {
        publisher.publishEvent(new SendEmailEvent(to, subject, body));
    }
}
