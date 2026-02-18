package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Entity.Business;
import com.shabanaj.beloyal.Entity.BusinessMember;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Enums.Role;

public interface BusinessMemberService {
    BusinessMember createBusinessMember(User user, Business business, Role role);
}
