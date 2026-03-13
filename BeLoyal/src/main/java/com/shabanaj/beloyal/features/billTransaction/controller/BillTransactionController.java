package com.shabanaj.beloyal.features.billTransaction.controller;

import com.shabanaj.beloyal.features.billTransaction.service.BillTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/")
@RequiredArgsConstructor
public class BillTransactionController {
    private final BillTransactionService billTransactionService;
    
}
