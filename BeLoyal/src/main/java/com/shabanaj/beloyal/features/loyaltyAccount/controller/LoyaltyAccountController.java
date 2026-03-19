package com.shabanaj.beloyal.features.loyaltyAccount.controller;

import com.shabanaj.beloyal.features.loyaltyAccount.service.LoyaltyAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/customers/")
@RequiredArgsConstructor
public class LoyaltyAccountController {
    private final LoyaltyAccountService loyaltyAccountService;

}
