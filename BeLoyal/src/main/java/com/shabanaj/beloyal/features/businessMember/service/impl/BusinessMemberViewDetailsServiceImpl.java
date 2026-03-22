package com.shabanaj.beloyal.features.businessMember.service.impl;

import com.shabanaj.beloyal.features.businessMember.dto.BusinessMemberDetailsDto;
import com.shabanaj.beloyal.features.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberViewDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessMemberViewDetailsServiceImpl implements BusinessMemberViewDetailsService {
    private final BusinessMemberRepository businessMemberRepository;

    @Override
    public List<BusinessMemberDetailsDto> getBusinessMemberDetails(Long businessId) {
        // check id
        if(businessId == null){
            throw new IllegalArgumentException("businessId cannot be null");
        }

        // find, map to dto and return
        return businessMemberRepository.findALlByBusinessId(businessId)
                .stream()
                .map(BusinessMemberDetailsDto::new)
                .toList();
    }
}
