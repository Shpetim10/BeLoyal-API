package com.shabanaj.beloyal.features.pointsBucket.controller;

import com.shabanaj.beloyal.features.pointsBucket.service.PointsBucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/")
@RequiredArgsConstructor
public class PointsBucketController {
    private final PointsBucketService pointsBucketService;

}
