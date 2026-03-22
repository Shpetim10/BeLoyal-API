package com.shabanaj.beloyal.features.businessMember.dto;

import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
public class BusinessMemberDetailsDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime lastLogin;
    private String role;
    private LocalDate hireDate;
    private String memberStatus;

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
        this.memberStatus=businessMember.getMemberStatus().name();
    }
}
