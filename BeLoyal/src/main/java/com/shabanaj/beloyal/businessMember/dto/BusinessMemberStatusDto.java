package com.shabanaj.beloyal.businessMember.dto;

import com.shabanaj.beloyal.model.Entity.BusinessMember;

public class BusinessMemberStatusDto {
    private BusinessMember.MemberStatus memberStatus;

    public BusinessMember.MemberStatus getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(BusinessMember.MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }
}
