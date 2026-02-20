package com.shabanaj.beloyal.registration.service.impl;

import com.shabanaj.beloyal.registration.dto.businessRegistration.SubmitBusinessApplicationRequest;
import com.shabanaj.beloyal.registration.service.BusinessApplicationValidatorService;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;

@Service
public class BusinessApplicationValidatorServiceImpl implements BusinessApplicationValidatorService {

    @Override
    public void validateBusinessApplicationOrThrow(SubmitBusinessApplicationRequest dto) {
        if (dto == null){
            throw new InvalidParameterException("The data for business registration is null");
        }

        if (dto.getBusinessRegistrationDto() == null){
            throw new InvalidParameterException("The data for business registration is null");
        }

        if (dto.getOwnershipToken() == null && dto.getUserDto() == null){
            throw new InvalidParameterException("The data for business user is null");
        }
    }
}
