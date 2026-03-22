package com.shabanaj.beloyal.features.business.dto;

import com.shabanaj.beloyal.features.businessMember.dto.BusinessMemberDetailsDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class BusinessDetailsDto {
    private Long id;
    private String businessName;
    private String businessType;
    private String businessDescription;
    private String logoPath;
    private String address;
    private String city;
    private String country;
    private String vatId;
    private String businessPhoneNumber;
    private String businessStatus;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String rejectionReason;
    // private Long reviewedByAdminId;
    private List<BusinessMemberDetailsDto> businessMembers;
}
