package com.shabanaj.beloyal.features.pointsTransaction.service;

import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionBusinessListViewDto;

import java.util.List;

public interface PointsTransactionBusinessViewService {
    List<PointTransactionBusinessListViewDto> getPointsTransactionList(Long businessId);
}
