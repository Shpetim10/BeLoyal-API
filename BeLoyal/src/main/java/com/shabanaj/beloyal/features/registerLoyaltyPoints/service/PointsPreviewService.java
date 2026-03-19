package com.shabanaj.beloyal.features.registerLoyaltyPoints.service;

import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.PointsPreviewRequest;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.PointsPreviewResponse;

public interface PointsPreviewService {
    PointsPreviewResponse preview(Long businessId, PointsPreviewRequest request);
}
