package com.shabanaj.beloyal.features.pointsTransaction.service.impl;

import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionCustomerAllListViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionCustomerBusinessListViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionCustomerViewService;
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
                PointTransactionCustomerAllListViewDto.builder()
                        .id(pt.getId())
                        .businessName(pt.getLoyaltyAccount().getBusiness().getBusinessName())
                        .businessLocation(pt.getLoyaltyAccount().getBusiness().getAddress()
                                + " , "
                                + pt.getLoyaltyAccount().getBusiness().getCity()
                                +" , "
                                + pt.getLoyaltyAccount().getBusiness().getCountry())
                        .businessLogoPath(pt.getLoyaltyAccount().getBusiness().getLogoPath())
                        .billTransactionReferenceId(pt.getBillTransaction().getInvoiceReference())
                        .type(pt.getType().name())
                        .points(pt.getPointsDelta())
                        .netAmount(pt.getBillTransaction().getNetAmount())
                        .discountAmount(pt.getBillTransaction().getDiscountAmount())
                        .billAmount(pt.getBillTransaction().getBillAmount())
                        .createdAt(pt.getCreatedAt())
                        .build()
        ).toList();
    }

    @Override
    @Transactional
    public List<PointTransactionCustomerBusinessListViewDto> getPointTransactionCustomerBusinessListVIewDto(Long userId, Long businessId) {
        if(userId==null || businessId==null){
            throw new IllegalArgumentException("userId and businessId is null");
        }

        return pointsTransactionRepository.findAllByUserIdAndBusinessId(userId, businessId)
                .map(pt-> PointTransactionCustomerBusinessListViewDto.builder()
                        .id(pt.getId())
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
