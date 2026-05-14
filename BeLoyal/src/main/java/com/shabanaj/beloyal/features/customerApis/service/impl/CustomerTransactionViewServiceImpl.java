package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerTransactionDto;
import com.shabanaj.beloyal.features.customerApis.service.CustomerTransactionViewService;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.model.Entity.BillTransaction;
import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import com.shabanaj.beloyal.model.Enums.PointsType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerTransactionViewServiceImpl implements CustomerTransactionViewService {

    private final PointsTransactionRepository pointsTransactionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerTransactionDto> getTransactions(Long userId, String typeFilter) {
        List<CustomerTransactionDto> transactions = pointsTransactionRepository
                .findPageByUserId(userId, PageRequest.of(0, 200))
                .getContent()
                .stream()
                .map(this::toDto)
                .toList();

        if (typeFilter != null && !typeFilter.isBlank()) {
            String filter = typeFilter.toUpperCase();
            return transactions.stream()
                    .filter(t -> t.type().equals(filter))
                    .toList();
        }

        return transactions;
    }

    private CustomerTransactionDto toDto(PointsTransaction pt) {
        BillTransaction bill = pt.getBillTransaction();
        return new CustomerTransactionDto(
                pt.getId(),
                pt.getLoyaltyAccount().getBusiness().getId(),
                pt.getLoyaltyAccount().getBusiness().getBusinessName(),
                mapType(pt.getType()),
                pt.getPointsDelta(),
                pt.getCreatedAt(),
                pt.getDescription(),
                bill != null ? bill.getNetAmount() : null,
                bill != null ? bill.getBillAmount() : null,
                bill != null ? bill.getDiscountAmount() : null,
                (bill!=null && bill.getBusiness()!=null)? bill.getBusiness().getCurrencyCode(): null,
                bill != null ? bill.getInvoiceReference() : null,
                pt.getReason(),
                pt.getScanMethod() != null ? pt.getScanMethod().name() : null,
                pt.getMoneyAmount(),
                pt.getRuleAmountPer(),
                pt.getRulePointsPer(),
                bill != null ? bill.getNote() : null
        );
    }

    private String mapType(PointsType type) {
        return switch (type) {
            case EARN_BILL -> "EARN";
            case REDEEM_DISCOUNT, REDEEM_OFFER -> "REDEEM";
            case COUPON_REDEMPTION -> "COUPON_PURCHASE";
            case EXPIRE -> "EXPIRED";
            case ADJUSTMENT_PLUS, ADJUSTMENT_MINUS, REVERSAL -> "ADJUSTMENT";
        };
    }
}
