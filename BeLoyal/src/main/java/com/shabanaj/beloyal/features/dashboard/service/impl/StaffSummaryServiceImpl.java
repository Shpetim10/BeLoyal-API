package com.shabanaj.beloyal.features.dashboard.service.impl;

import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.dashboard.dto.StaffSummaryDto;
import com.shabanaj.beloyal.features.dashboard.service.StaffSummaryService;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StaffSummaryServiceImpl implements StaffSummaryService {

    private final PointsTransactionRepository pointsTransactionRepository;
    private final CustomerCouponRepository customerCouponRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;

    @Override
    public StaffSummaryDto getSummary(Long businessId, Long staffUserId) {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        long todayScansCount = pointsTransactionRepository.countTodayByBusinessIdAndMemberUserId(businessId, staffUserId, startOfToday);
        long pendingRedemptionsCount = customerCouponRepository.countByBusinessIdAndStatus(businessId, CustomerCouponStatus.REDEEMED);
        long transactionsCount = pointsTransactionRepository.countByBusinessIdAndMemberUserId(businessId, staffUserId);
        long activeCustomersCount = loyaltyAccountRepository.countByBusinessId(businessId);

        return StaffSummaryDto.builder()
                .todayScansCount(todayScansCount)
                .pendingRedemptionsCount(pendingRedemptionsCount)
                .transactionsCount(transactionsCount)
                .activeCustomersCount(activeCustomersCount)
                .build();
    }
}
