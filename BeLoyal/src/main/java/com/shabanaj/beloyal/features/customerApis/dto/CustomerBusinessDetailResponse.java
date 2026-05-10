package com.shabanaj.beloyal.features.customerApis.dto;

import java.util.List;

public record CustomerBusinessDetailResponse(
        CustomerBusinessDetailDto business,
        CustomerLoyaltyDto loyalty,
        CustomerCatalogDto catalog,
        List<CustomerBusinessCouponDto> coupons,
        List<CustomerBusinessTransactionDto> transactions,
        CustomerDetailsTabDto details
) {
}
