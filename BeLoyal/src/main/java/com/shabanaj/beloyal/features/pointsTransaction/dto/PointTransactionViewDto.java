package com.shabanaj.beloyal.features.pointsTransaction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PointTransactionViewDto {
    private Long id;
    private String customerFullName;
    private String customerEmail;
    private String customerPhone;
    private String businessName;
    private Integer availablePoints;
    private Integer lifetimeEarnedPoints;
    private Integer lifetimeExpired;
    private LocalDateTime lastActivityAt;
    private String invoiceReference;
    private String note;
    private BigDecimal netAmount;
    private BigDecimal discountAmount;
    private BigDecimal billAmount;
    private String businessMemberFullName;
    private String type;
    private String description;
    private String reason;
    private Integer pointsDelta;
    private BigDecimal ruleAmountPer;
    private Integer rulePointsPer;
    private LocalDateTime createdAt;
}
