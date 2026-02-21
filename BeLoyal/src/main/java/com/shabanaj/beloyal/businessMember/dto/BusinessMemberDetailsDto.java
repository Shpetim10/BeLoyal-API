package com.shabanaj.beloyal.businessMember.dto;

import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class BusinessMemberDetailsDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime lastLogin;
    private String role;
    private LocalDate hireDate;
    private BusinessMember.MemberStatus memberStatus;

    public BusinessMemberDetailsDto(BusinessMember businessMember){
        User user=businessMember.getUser();
        if(user==null){
            return;
        }

        this.id=businessMember.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email= user.getEmail();
        this.lastLogin= user.getLastLoginAt();
        this.role=businessMember.getRole().name();
        this.hireDate=businessMember.getHiredAt();
        this.memberStatus=businessMember.getMemberStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public BusinessMember.MemberStatus getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(BusinessMember.MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }
}
