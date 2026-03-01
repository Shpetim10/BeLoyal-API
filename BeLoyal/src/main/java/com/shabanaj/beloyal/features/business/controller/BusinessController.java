package com.shabanaj.beloyal.features.business.controller;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/admin")
@RequiredArgsConstructor
public class BusinessController {
    private final BusinessService businessService;

    @GetMapping("/businesses/status")
    public ResponseEntity<List<Business>> getBusinessesByStatus(@RequestParam BusinessStatus businessStatus) {
        return ResponseEntity.ok(businessService.getBusinessesByStatus(businessStatus));
    }

    @GetMapping("/businesses")
    public ResponseEntity<List<Business>> getAllBusinesses() {
        return ResponseEntity.ok(businessService.getAllBusinesses());
    }
}
