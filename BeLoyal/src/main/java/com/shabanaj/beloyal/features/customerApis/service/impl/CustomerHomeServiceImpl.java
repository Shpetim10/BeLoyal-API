package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerBusinessDto;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerCategoryDto;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerHomeResponse;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerPromotionDto;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerSummaryDto;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerTransactionDto;
import com.shabanaj.beloyal.features.customerApis.service.BusinessViewService;
import com.shabanaj.beloyal.features.customerApis.service.CustomerHomeService;
import com.shabanaj.beloyal.features.customerApis.service.CustomerPromotionViewService;
import com.shabanaj.beloyal.features.customerApis.service.CustomerStatsService;
import com.shabanaj.beloyal.features.customerApis.service.CustomerTransactionViewService;
import com.shabanaj.beloyal.model.Enums.BusinessCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerHomeServiceImpl implements CustomerHomeService {

    private final CustomerStatsService customerStatsService;
    private final BusinessViewService businessViewService;
    private final CustomerPromotionViewService customerPromotionViewService;
    private final CustomerTransactionViewService customerTransactionViewService;

    @Override
    @Transactional(readOnly = true)
    public CustomerHomeResponse getHome(Long userId) {
        CustomerSummaryDto summary = customerStatsService.getCustomerStats(userId);
        List<CustomerBusinessDto> businesses = businessViewService.getBusinessesForCustomer(userId);
        List<CustomerPromotionDto> promotions = customerPromotionViewService.getPromotions(userId, null);
        List<CustomerTransactionDto> transactions = customerTransactionViewService.getTransactions(userId, null);
        List<CustomerCategoryDto> categories = buildCategories(businesses);

        return new CustomerHomeResponse(summary, categories, businesses, promotions, transactions);
    }

    private List<CustomerCategoryDto> buildCategories(List<CustomerBusinessDto> businesses) {
        Map<String, Long> countByKey = businesses.stream()
                .collect(Collectors.groupingBy(CustomerBusinessDto::categoryKey, Collectors.counting()));

        return Arrays.stream(BusinessCategory.values())
                .filter(cat -> countByKey.containsKey(cat.name()))
                .map(cat -> new CustomerCategoryDto(
                        cat.ordinal(),
                        cat.name(),
                        toTitleCase(cat.name()),
                        countByKey.get(cat.name()).intValue(),
                        cat.ordinal()
                ))
                .toList();
    }

    private String toTitleCase(String name) {
        return Arrays.stream(name.split("_"))
                .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
