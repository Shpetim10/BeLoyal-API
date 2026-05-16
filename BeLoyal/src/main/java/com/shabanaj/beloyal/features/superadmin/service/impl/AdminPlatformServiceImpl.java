package com.shabanaj.beloyal.features.superadmin.service.impl;

import com.shabanaj.beloyal.features.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.superadmin.dto.BusinessMembershipSummaryDto;
import com.shabanaj.beloyal.features.superadmin.dto.LoyaltySummaryDto;
import com.shabanaj.beloyal.features.superadmin.dto.PlatformUserSummaryDto;
import com.shabanaj.beloyal.features.superadmin.service.AdminPlatformService;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPlatformServiceImpl implements AdminPlatformService {

    private final UserRepository userRepository;
    private final BusinessMemberRepository businessMemberRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PlatformUserSummaryDto> getAllUsers() {
        List<User> users = userRepository.findAllOrderedByCreatedAt();
        return users.stream().map(this::toSummary).collect(Collectors.toList());
    }

    private PlatformUserSummaryDto toSummary(User user) {
        List<BusinessMember> memberships = businessMemberRepository.findByUser(user);

        List<BusinessMembershipSummaryDto> membershipDtos = memberships.stream()
                .filter(m -> m.getRole() == Role.BUSINESS_ADMIN || m.getRole() == Role.STAFF)
                .map(m -> new BusinessMembershipSummaryDto(
                        m.getBusiness().getId(),
                        m.getBusiness().getBusinessName(),
                        m.getRole().name(),
                        m.getMemberStatus().name()
                ))
                .collect(Collectors.toList());

        LoyaltySummaryDto loyaltySummary = null;
        if (user.getRoles().contains(Role.CUSTOMER)) {
            long earned = loyaltyAccountRepository.sumLifetimeEarnedByUserId(user.getId());
            long spent = loyaltyAccountRepository.sumLifetimeRedeemedByUserId(user.getId());
            long expired = loyaltyAccountRepository.sumLifetimeExpiredByUserId(user.getId());
            long available = loyaltyAccountRepository.sumAvailablePointsByUserId(user.getId());
            long businessCount = loyaltyAccountRepository.countByUserId(user.getId());
            loyaltySummary = new LoyaltySummaryDto(earned, spent, expired, available, businessCount);
        }

        return new PlatformUserSummaryDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoles().stream().map(Role::name).collect(Collectors.toSet()),
                user.getStatus().name(),
                user.isEmailVerified(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                membershipDtos,
                loyaltySummary
        );
    }
}
