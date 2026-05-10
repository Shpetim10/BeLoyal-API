package com.shabanaj.beloyal.features.customerApis.service;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerPromotionDto;

import java.util.List;

public interface CustomerPromotionViewService {
    List<CustomerPromotionDto> getPromotions(Long userId, String statusFilter);
}
