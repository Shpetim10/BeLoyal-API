package com.shabanaj.beloyal.features.pointsTransaction.service.impl;

import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionService;
import com.shabanaj.beloyal.model.Entity.BillTransaction;
import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;

@Service
@RequiredArgsConstructor
public class PointsTransactionsServiceImpl implements PointsTransactionService {
    private final PointsTransactionRepository pointsTransactionRepository;

    @Override
    public PointsTransaction save(PointsTransaction pointsTransaction) {
        return pointsTransactionRepository.save(pointsTransaction);
    }

    @Override
    public PointTransactionViewDto getById(Long id) {
        if(id == null)
            throw new IllegalArgumentException("id is null");

        PointsTransaction pointsTransaction= pointsTransactionRepository.getPointsTransactionById(id);
        BillTransaction billTransaction = pointsTransaction.getBillTransaction();

        return PointTransactionViewDto.builder()
                .id(pointsTransaction.getId())
                .customerFullName(
                        pointsTransaction.getLoyaltyAccount().getCustomerProfile().getUser().getFirstName()+ " "
                        + pointsTransaction.getLoyaltyAccount().getCustomerProfile().getUser().getLastName()
                )
                .customerEmail(pointsTransaction.getLoyaltyAccount().getCustomerProfile().getUser().getEmail())
                .customerPhone(pointsTransaction.getLoyaltyAccount().getCustomerProfile().getUser().getPhoneNumber())
                .businessName(pointsTransaction.getLoyaltyAccount().getBusiness().getBusinessName())
                .availablePoints(pointsTransaction.getLoyaltyAccount().getAvailablePoints())
                .lifetimeEarnedPoints(pointsTransaction.getLoyaltyAccount().getLifetimeEarned())
                .lifetimeExpired(pointsTransaction.getLoyaltyAccount().getLifetimeExpired())
                .lastActivityAt(pointsTransaction.getLoyaltyAccount().getLastActivityAt())
                .invoiceReference(billTransaction != null ? billTransaction.getInvoiceReference() : null)
                .note(billTransaction != null ? billTransaction.getNote() : null)
                .netAmount(billTransaction != null ? billTransaction.getNetAmount() : null)
                .discountAmount(billTransaction != null ? billTransaction.getDiscountAmount() : null)
                .billAmount(billTransaction != null ? billTransaction.getBillAmount() : null)
                .businessMemberFullName(
                        pointsTransaction.getBusinessMember().getUser().getFirstName()+ " "
                        + pointsTransaction.getBusinessMember().getUser().getLastName()
                )
                .type(pointsTransaction.getType().name())
                .description(pointsTransaction.getDescription())
                .reason(pointsTransaction.getReason())
                .pointsDelta(pointsTransaction.getPointsDelta())
                .rulePointsPer(pointsTransaction.getRulePointsPer())
                .ruleAmountPer(pointsTransaction.getRuleAmountPer())
                .createdAt(pointsTransaction.getCreatedAt())
                .build();
    }
}
