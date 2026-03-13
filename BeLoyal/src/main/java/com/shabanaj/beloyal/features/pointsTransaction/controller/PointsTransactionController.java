package com.shabanaj.beloyal.features.pointsTransaction.controller;

import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/")
@RequiredArgsConstructor
public class PointsTransactionController {
    private final PointsTransactionService pointsTransactionService;

}
