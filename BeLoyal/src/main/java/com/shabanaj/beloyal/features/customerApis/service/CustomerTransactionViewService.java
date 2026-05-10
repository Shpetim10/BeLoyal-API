package com.shabanaj.beloyal.features.customerApis.service;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerTransactionDto;

import java.util.List;

public interface CustomerTransactionViewService {
    List<CustomerTransactionDto> getTransactions(Long userId, String typeFilter);
}
