package com.shabanaj.beloyal.auth.service;

import com.shabanaj.beloyal.auth.dto.BusinessProfileInfo;
import com.shabanaj.beloyal.auth.dto.LoginResponse;
import com.shabanaj.beloyal.model.Entity.User;

import java.util.List;

public interface TokenIssuerService {
    LoginResponse issue(User user, List<BusinessProfileInfo> businessProfileInfos);
}
