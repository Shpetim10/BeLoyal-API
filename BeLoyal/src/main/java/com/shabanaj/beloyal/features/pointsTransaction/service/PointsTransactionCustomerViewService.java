package com.shabanaj.beloyal.features.pointsTransaction.service;

import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionCustomerAllListViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionCustomerBusinessListViewDto;

import java.util.List;

public interface PointsTransactionCustomerViewService {
    List<PointTransactionCustomerAllListViewDto> getPointTransactionCustomerListViewDto(Long userId);
    List<PointTransactionCustomerBusinessListViewDto> getPointTransactionCustomerBusinessListVIewDto(Long userId, Long businessId);
}
