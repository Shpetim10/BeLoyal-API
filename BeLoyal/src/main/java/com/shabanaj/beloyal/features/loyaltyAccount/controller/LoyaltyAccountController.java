package com.shabanaj.beloyal.features.loyaltyAccount.controller;

import com.shabanaj.beloyal.features.loyaltyAccount.service.LoyaltyAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/")
@RequiredArgsConstructor
public class LoyaltyAccountController {
    private final LoyaltyAccountService loyaltyAccountService;

    
}
