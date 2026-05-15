package com.shabanaj.beloyal.features.dashboard.service.impl;

import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.dashboard.dto.BusinessAdminDashboardDto;
import com.shabanaj.beloyal.features.dashboard.service.BusinessAdminDashboardService;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.PointsType;
import com.shabanaj.beloyal.model.Enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessAdminDashboardServiceImpl implements BusinessAdminDashboardService {

    private final BusinessMemberRepository businessMemberRepository;
    private final CouponRepository couponRepository;
    private final PointsTransactionRepository pointsTransactionRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;

    @Override
    public BusinessAdminDashboardDto getSummary(Long businessId) {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        long staffCount = businessMemberRepository.countByBusinessIdAndRole(businessId, Role.STAFF);
        long activeCouponsCount = couponRepository.countByBusinessIdAndStatusAndDeletedAtIsNull(businessId, CouponStatus.ACTIVE);
        long transactionsTotal = pointsTransactionRepository.countByBusinessId(businessId);
        long loyalCustomersCount = loyaltyAccountRepository.countByBusinessId(businessId);
        long todayPointsIssued = pointsTransactionRepository.sumPointsDeltaByBusinessIdAndTypesAndFrom(
                businessId,
                List.of(PointsType.EARN_BILL, PointsType.ADJUSTMENT_PLUS),
                startOfToday
        );

        return BusinessAdminDashboardDto.builder()
                .staffCount(staffCount)
                .activeCouponsCount(activeCouponsCount)
                .transactionsTotal(transactionsTotal)
                .loyalCustomersCount(loyalCustomersCount)
                .todayPointsIssued(todayPointsIssued)
                .build();
    }
}
