package com.shabanaj.beloyal.features.business.controller;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.business.service.BusinessDeletionService;
import com.shabanaj.beloyal.features.business.service.BusinessLifecycleService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/besahub/admin")
@RequiredArgsConstructor
public class BusinessController {
    private final BusinessService businessService;
    private final BusinessLifecycleService businessLifecycleService;
    private final BusinessDeletionService businessDeletionService;

    @GetMapping("/businesses/status")
    public ResponseEntity<List<Business>> getBusinessesByStatus(@RequestParam BusinessStatus businessStatus) {
        return ResponseEntity.ok(businessService.getBusinessesByStatus(businessStatus));
    }

    @PatchMapping("/businesses/{id}/suspend")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> suspendBusiness(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reason = request.getOrDefault("reason", "");
        businessLifecycleService.suspendBusiness(id, reason);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/businesses/{id}/ban")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> banBusiness(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reason = request.getOrDefault("reason", "");
        businessLifecycleService.banBusiness(id, reason);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/businesses/{id}/reactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> reactivateBusiness(@PathVariable Long id) {
        businessLifecycleService.reactivateBusiness(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/businesses/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteBusinessHard(@PathVariable Long id) {
        businessDeletionService.deleteBusinessHard(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/businesses")
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
//    public ResponseEntity<List<Business>> getAllBusinesses() {
//        return ResponseEntity.ok(businessService.getAllBusinesses());
//    }
}
