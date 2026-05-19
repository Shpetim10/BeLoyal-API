package com.shabanaj.beloyal.features.businessMember.service.impl;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.common.Exception.BusinessMemberProfileNotFound;
import com.shabanaj.beloyal.common.redis.jwtToken.TokenVersionService;
import com.shabanaj.beloyal.features.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberDeletionService;
import com.shabanaj.beloyal.features.token.repository.RefreshTokenRepository;
import com.shabanaj.beloyal.features.token.repository.ResetPasswordTokenRepository;
import com.shabanaj.beloyal.features.token.repository.StaffInviteTokenRepository;
import com.shabanaj.beloyal.features.token.repository.VerificationTokenRepository;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.features.userProfiles.customer.repository.CustomerProfileRepository;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessMemberDeletionServiceImpl implements BusinessMemberDeletionService {

    private final BusinessMemberRepository businessMemberRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final StaffInviteTokenRepository staffInviteTokenRepository;
    private final UserRepository userRepository;
    private final TokenVersionService tokenVersionService;

    @Override
    @Transactional
    public void deleteStaffMember(Long businessId, Long memberId) {
        BusinessMember member = businessMemberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessMemberProfileNotFound("Business member not found"));

        if (!member.getBusiness().getId().equals(businessId)) {
            throw new BusinessMemberProfileNotFound("Business member not found");
        }

        if (member.getRole() == Role.BUSINESS_ADMIN) {
            throw new BadRequestException("Cannot remove the business admin via this endpoint");
        }

        Long userId = member.getUser().getId();
        log.info("Deleting staff member: memberId={}, userId={}, businessId={}", memberId, userId, businessId);

        // Invalidate the staff member's JWT sessions
        tokenVersionService.bumpVersion(userId);

        // Delete the business member record
        businessMemberRepository.deleteById(memberId);
        log.debug("Deleted business member record: memberId={}", memberId);

        // Delete the user only when they have no remaining identity anchor
        boolean hasOtherMemberships = businessMemberRepository.countOtherMembershipsForUser(userId, businessId) > 0;
        boolean hasCustomerProfile = customerProfileRepository.findByUserId(userId).isPresent();

        if (!hasOtherMemberships && !hasCustomerProfile) {
            log.info("User has no remaining memberships or customer profile, deleting user: userId={}", userId);
            deleteUser(userId);
        } else {
            log.info("User retained: userId={}, hasOtherMemberships={}, hasCustomerProfile={}", userId, hasOtherMemberships, hasCustomerProfile);
        }
    }

    private void deleteUser(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        resetPasswordTokenRepository.deleteByUserId(userId);
        verificationTokenRepository.deleteByUserId(userId);
        staffInviteTokenRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
        log.info("Deleted user and associated tokens: userId={}", userId);
    }
}
