package com.shabanaj.beloyal.features.business.service.impl;

import com.shabanaj.beloyal.features.business.dto.BusinessDetailsDto;
import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.businessMember.dto.BusinessListView;
import com.shabanaj.beloyal.features.business.service.BusinessViewDetailsService;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberViewDetailsService;
import com.shabanaj.beloyal.model.Entity.Business;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessViewDetailsServiceImpl implements BusinessViewDetailsService {
    private final BusinessService businessService;
    private final BusinessMemberViewDetailsService businessMemberViewDetailsService;

    @Override
    public List<BusinessListView> getBusinesses() {
        // get businesses
        List<Business> businesses= businessService.getAllBusinesses();

        // map to dto and return
        return businesses.stream()
                .map(b-> BusinessListView.builder()
                        .id(b.getId())
                        .businessName(b.getBusinessName())
                        .businessAddress(b.getAddress()+", "+b.getCity()+", "+b.getCountry())
                        .businessEmail(b.getBusinessEmail())
                        .businessPhone(b.getBusinessPhoneNumber())
                        .businessStatus(b.getBusinessStatus().name())
                        .logoPath(b.getLogoPath())
                        .build())
                .toList();
    }

    @Override
    public BusinessDetailsDto getBusinessDetails(Long businessId) {
        // check id
        if(businessId==null){
            throw new IllegalArgumentException("businessId is null");
        }

        // find or throw
        Business business= businessService.getBusinessById(businessId);

        return BusinessDetailsDto.builder()
                .id(business.getId())
                .businessName(business.getBusinessName())
                .businessType(business.getBusinessType().name())
                .businessDescription(business.getBusinessDescription())
                .logoPath(business.getLogoPath())
                .address(business.getAddress())
                .city(business.getCity())
                .country(business.getCountry())
                .vatId(business.getVatId())
                .businessPhoneNumber(business.getBusinessPhoneNumber())
                .businessStatus(business.getBusinessStatus().name())
                .submittedAt(business.getSubmittedAt())
                .reviewedAt(business.getReviewedAt())
                .rejectionReason(business.getRejectionReason())
                .currencyCode(business.getCurrencyCode())
                .businessMembers(businessMemberViewDetailsService.getBusinessMemberDetails(businessId))
                .build();
    }
}
