package com.shabanaj.beloyal.features.pointsTransaction.service.impl;

import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionCustomerAllListViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionCustomerBusinessListViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionCustomerViewService;
import com.shabanaj.beloyal.model.Entity.BillTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointTransactionCustomerViewServiceImpl implements PointsTransactionCustomerViewService {
    private final PointsTransactionRepository pointsTransactionRepository;

    @Override
    @Transactional
    public List<PointTransactionCustomerAllListViewDto> getPointTransactionCustomerListViewDto(Long userId) {
        if(userId==null){
            throw new IllegalArgumentException("userId is null");
        }

        return pointsTransactionRepository.findAllByUserId(userId).map(pt ->
                {
                    BillTransaction billTransaction = pt.getBillTransaction();
                    return
                PointTransactionCustomerAllListViewDto.builder()
                        .id(pt.getId())
                        .businessName(pt.getLoyaltyAccount().getBusiness().getBusinessName())
                        .businessLocation(pt.getLoyaltyAccount().getBusiness().getAddress()
                                + " , "
                                + pt.getLoyaltyAccount().getBusiness().getCity()
                                +" , "
                                + pt.getLoyaltyAccount().getBusiness().getCountry())
                        .businessLogoPath(pt.getLoyaltyAccount().getBusiness().getLogoPath())
                        .billTransactionReferenceId(billTransaction != null ? billTransaction.getInvoiceReference() : null)
                        .type(pt.getType().name())
                        .points(pt.getPointsDelta())
                        .netAmount(billTransaction != null ? billTransaction.getNetAmount() : null)
                        .discountAmount(billTransaction != null ? billTransaction.getDiscountAmount() : null)
                        .billAmount(billTransaction != null ? billTransaction.getBillAmount() : null)
                        .createdAt(pt.getCreatedAt())
                        .build();
                }).toList();
    }

    @Override
    @Transactional
    public List<PointTransactionCustomerBusinessListViewDto> getPointTransactionCustomerBusinessListVIewDto(Long userId, Long businessId) {
        if(userId==null || businessId==null){
            throw new IllegalArgumentException("userId and businessId is null");
        }

        return pointsTransactionRepository.findAllByUserIdAndBusinessId(userId, businessId)
                .map(pt-> {
                        BillTransaction billTransaction = pt.getBillTransaction();
                        return PointTransactionCustomerBusinessListViewDto.builder()
                        .id(pt.getId())
                        .billTransactionReferenceId(billTransaction != null ? billTransaction.getInvoiceReference() : null)
                        .type(pt.getType().name())
                        .points(pt.getPointsDelta())
                        .netAmount(billTransaction != null ? billTransaction.getNetAmount() : null)
                        .discountAmount(billTransaction != null ? billTransaction.getDiscountAmount() : null)
                        .billAmount(billTransaction != null ? billTransaction.getBillAmount() : null)
                        .createdAt(pt.getCreatedAt())
                        .build();
                })
                .toList();
    }
}
