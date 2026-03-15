package com.shabanaj.beloyal.features.customerLookup.dto;

import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerLookupDto {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private Integer currentPoints;

    public static CustomerLookupDto fromCustomerProfileAndLoyaltyAccount(CustomerProfile customerProfile, LoyaltyAccount loyaltyAccount) {
        CustomerLookupDto customerLookupDto = new CustomerLookupDto();
        customerLookupDto.setCustomerId(customerProfile.getId());
        customerLookupDto.setFirstName(customerProfile.getUser().getFirstName());
        customerLookupDto.setLastName(customerProfile.getUser().getLastName());
        customerLookupDto.setEmail(customerProfile.getUser().getEmail());
        customerLookupDto.setCurrentPoints(loyaltyAccount.getAvailablePoints());
        return customerLookupDto;
    }
}
