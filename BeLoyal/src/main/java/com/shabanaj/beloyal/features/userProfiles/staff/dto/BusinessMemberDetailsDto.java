package com.shabanaj.beloyal.features.userProfiles.staff.dto;

import com.shabanaj.beloyal.model.Entity.BusinessMember;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BusinessMemberDetailsDto {
    private String businessName;
    private String role;
    private String memberStatus;
    private LocalDate hiredAt;

    public BusinessMemberDetailsDto(BusinessMember member){
        this.businessName=member.getBusiness().getBusinessName();
        this.role=member.getRole().name();
        this.memberStatus=member.getMemberStatus().name();
        this.hiredAt=member.getHiredAt();
    }
}
