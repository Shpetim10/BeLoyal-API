package com.shabanaj.beloyal.features.superadmin.controller;

import com.shabanaj.beloyal.features.superadmin.dto.PlatformUserSummaryDto;
import com.shabanaj.beloyal.features.superadmin.service.AdminPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/admin")
@RequiredArgsConstructor
public class AdminPlatformController {

    private final AdminPlatformService adminPlatformService;

    @GetMapping("/platform/users")
    public ResponseEntity<List<PlatformUserSummaryDto>> getAllUsers() {
        return ResponseEntity.ok(adminPlatformService.getAllUsers());
    }
}
