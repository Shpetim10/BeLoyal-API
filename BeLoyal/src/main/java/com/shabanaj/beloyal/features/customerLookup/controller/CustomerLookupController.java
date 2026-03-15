package com.shabanaj.beloyal.features.customerLookup.controller;

import com.shabanaj.beloyal.features.customerLookup.dto.CustomerLookupDto;
import com.shabanaj.beloyal.features.customerLookup.service.CustomerLookupForBusinessService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/customers/lookup")
public class CustomerLookupController {
    private final CustomerLookupForBusinessService qrTokenLookupForBusinessService;
    private final CustomerLookupForBusinessService manualCodeLookupForBusinessService;
    private final CustomerLookupForBusinessService emailLookupForBusinessService;


    public CustomerLookupController(
            @Qualifier("qrTokenLookupForBusiness") CustomerLookupForBusinessService qrTokenLookupForBusinessService,
            @Qualifier("manualCodeLookupForBusiness") CustomerLookupForBusinessService manualCodeLookupForBusinessService,
            @Qualifier("emailLookupForBusiness") CustomerLookupForBusinessService emailLookupForBusinessService) {
        this.qrTokenLookupForBusinessService = qrTokenLookupForBusinessService;
        this.manualCodeLookupForBusinessService = manualCodeLookupForBusinessService;
        this.emailLookupForBusinessService = emailLookupForBusinessService;
    }

    @GetMapping(params = "qrToken")
    public ResponseEntity<CustomerLookupDto> getCustomerLookupByQrToken(
            @PathVariable("businessId") Long businessId,
            @RequestParam("qrToken") String qrToken){
        CustomerLookupDto customerLookupDto= qrTokenLookupForBusinessService.getCustomerForBusiness(
                businessId,
                qrToken
        );

        return ResponseEntity.ok(customerLookupDto);
    }

    @GetMapping(params="manualCode")
    public ResponseEntity<CustomerLookupDto> getCustomerLookupByManualCode(
            @PathVariable("businessId") Long businessId,
            @RequestParam("manualCode") String manualCode
    ){
        CustomerLookupDto customerLookupDto= manualCodeLookupForBusinessService.getCustomerForBusiness(
                businessId,
                manualCode
        );

        return ResponseEntity.ok(customerLookupDto);
    }

    @GetMapping(params="email")
    public ResponseEntity<CustomerLookupDto> getCustomerLookupByEmail(
            @PathVariable("businessId") Long businessId,
            @RequestParam("email") String email
    ){
        CustomerLookupDto customerLookupDto= emailLookupForBusinessService.getCustomerForBusiness(
                businessId,
                email
        );

        return ResponseEntity.ok(customerLookupDto);
    }
}
