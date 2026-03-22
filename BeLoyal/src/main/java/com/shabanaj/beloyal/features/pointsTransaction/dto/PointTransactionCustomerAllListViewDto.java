package com.shabanaj.beloyal.features.pointsTransaction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PointTransactionCustomerAllListViewDto {
    private Long id;
    private String businessName;
    private String businessLocation;
    private String businessLogoPath;
    private String billTransactionReferenceId;
    private String type;
    private Integer points;
    private BigDecimal netAmount;
    private BigDecimal discountAmount;
    private BigDecimal billAmount;
    private LocalDateTime createdAt;
}
