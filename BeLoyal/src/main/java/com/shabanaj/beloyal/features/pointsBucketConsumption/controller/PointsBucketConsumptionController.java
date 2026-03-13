package com.shabanaj.beloyal.features.pointsBucketConsumption.controller;

import com.shabanaj.beloyal.features.pointsBucketConsumption.service.PointsBucketConsumptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/")
@RequiredArgsConstructor
public class PointsBucketConsumptionController {
    private final PointsBucketConsumptionService pointsBucketConsumptionService;
    
}
