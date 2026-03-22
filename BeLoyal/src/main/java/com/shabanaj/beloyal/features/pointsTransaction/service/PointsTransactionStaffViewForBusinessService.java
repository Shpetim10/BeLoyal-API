package com.shabanaj.beloyal.features.pointsTransaction.service;

import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionStaffListView;

import java.util.List;

public interface PointsTransactionStaffViewForBusinessService {
    List<PointTransactionStaffListView> getTransactionList(Long memberId, Long businessId);
}
