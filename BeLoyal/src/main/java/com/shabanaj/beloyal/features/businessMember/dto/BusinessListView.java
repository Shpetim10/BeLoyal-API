package com.shabanaj.beloyal.features.businessMember.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BusinessListView {
    private Long id;
    private String businessName;
    private String businessAddress;
    private String businessPhone;
    private String businessEmail;
    private String businessStatus;
    private String logoPath;
}
