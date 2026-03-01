package com.shabanaj.beloyal.features.auth.service;

import com.shabanaj.beloyal.features.auth.dto.BusinessProfileInfo;
import com.shabanaj.beloyal.features.auth.dto.LoginResponse;
import com.shabanaj.beloyal.model.Entity.User;

import java.util.List;

public interface TokenIssuerService {
    LoginResponse issue(User user, List<BusinessProfileInfo> businessProfileInfos);
}
