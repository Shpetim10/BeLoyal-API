package com.shabanaj.beloyal.features.pointsTransaction.service.impl;

import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionStaffListView;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionStaffViewForBusinessService;
import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointsTransactionStaffViewForBusinessServiceImpl implements PointsTransactionStaffViewForBusinessService {
    private final PointsTransactionRepository pointsTransactionRepository;

    @Override
    public List<PointTransactionStaffListView> getTransactionList(Long memberId, Long businessId) {
        if(businessId == null || memberId == null ){
            throw new IllegalArgumentException("Business id or member id is null");
        }

        // get transaction list
        List<PointsTransaction> pointsTransactions= pointsTransactionRepository.findAllByBusinessMemberUserIdAndBusinessId(memberId, businessId);

        // map to dto
        return pointsTransactions.stream().map(pt->PointTransactionStaffListView.builder()
                        .id(pt.getId())
                        .customerFullName(
                                pt.getLoyaltyAccount().getCustomerProfile().getUser().getFirstName()+ " "
                                        + pt.getLoyaltyAccount().getCustomerProfile().getUser().getLastName())
                        .billTransactionReferenceId(pt.getBillTransaction().getInvoiceReference())
                        .type(pt.getType().name())
                        .points(pt.getPointsDelta())
                        .netAmount(pt.getBillTransaction().getNetAmount())
                        .discountAmount(pt.getBillTransaction().getDiscountAmount())
                        .billAmount(pt.getBillTransaction().getBillAmount())
                        .createdAt(pt.getCreatedAt())
                .build())
                .toList();
    }
}
