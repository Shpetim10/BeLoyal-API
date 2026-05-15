package com.shabanaj.beloyal.features.dashboard.controller;

import com.shabanaj.beloyal.features.dashboard.dto.PlatformSummaryDto;
import com.shabanaj.beloyal.features.dashboard.service.PlatformSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/admin")
@RequiredArgsConstructor
public class PlatformSummaryController {

    private final PlatformSummaryService platformSummaryService;

    @GetMapping("/platform-summary")
    public ResponseEntity<PlatformSummaryDto> getPlatformSummary() {
        return ResponseEntity.ok(platformSummaryService.getSummary());
    }
}
