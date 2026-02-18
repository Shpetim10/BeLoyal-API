package com.shabanaj.beloyal.registration.service;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;

public interface BusinessMemberService {
    BusinessMember createBusinessMember(User user, Business business, Role role);
}
