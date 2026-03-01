package com.shabanaj.beloyal.common.Helpers;

import com.shabanaj.beloyal.features.userProfiles.customer.repository.CustomerProfileRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ReferralCodeGenerator {
    private final CustomerProfileRepository customerProfileRepository;
    private final String CHARACTERS= "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int CODE_LENGTH=6;
    private final SecureRandom random= new SecureRandom();

    public ReferralCodeGenerator(CustomerProfileRepository customerProfileRepository) {
        this.customerProfileRepository = customerProfileRepository;
    }

    public String generateReferralCode(){
        StringBuffer code= new StringBuffer(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        if(customerProfileRepository.findByReferralCode(code.toString()).isPresent()){
            return generateReferralCode();
        }

        return code.toString();
    }
}
