package com.shabanaj.beloyal.features.pointsTransaction.service.impl;

import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionBusinessListViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionBusinessViewService;
import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointsTransactionBusinessViewServiceImpl implements PointsTransactionBusinessViewService {
    private final PointsTransactionRepository pointsTransactionRepository;

    @Override
    public List<PointTransactionBusinessListViewDto> getPointsTransactionList(Long businessId) {
        if(businessId == null){
            throw new IllegalArgumentException("businessId can't be null");
        }

        List<PointsTransaction> transactions= pointsTransactionRepository.findAllByBusinessId(businessId);

        return transactions.stream().map(pt->
                    PointTransactionBusinessListViewDto.builder()
                            .id(pt.getId())
                            .customerFullName(
                                    pt.getLoyaltyAccount().getCustomerProfile().getUser().getFirstName()+ " "
                                    + pt.getLoyaltyAccount().getCustomerProfile().getUser().getLastName())
                            .billTransactionReferenceId(pt.getBillTransaction().getInvoiceReference())
                            .businessMemberFullName(pt.getBusinessMember().getUser().getFirstName()+ " "+ pt.getBusinessMember().getUser().getLastName())
                            .type(pt.getType().name())
                            .points(pt.getPointsDelta())
                            .netAmount(pt.getBillTransaction().getNetAmount())
                            .discountAmount(pt.getBillTransaction().getDiscountAmount())
                            .billAmount(pt.getBillTransaction().getBillAmount())
                            .createdAt(pt.getCreatedAt())
                            .build()
                ).toList();
    }
}
