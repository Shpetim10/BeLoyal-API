package com.shabanaj.beloyal.businessMember.service;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;

public interface BusinessMemberStatusServcie {
    void changeStatusAndSave(User user, Business business, BusinessMember.MemberStatus memberStatus);
    void changeStatusAndSave(Long userId, Long businessId, BusinessMember.MemberStatus memberStatus);
    void changeStatusAndSave(Long memberId, BusinessMember.MemberStatus memberStatus);
}
