package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Dto.WaiterProfileRegisterDto;
import com.shabanaj.beloyal.Entity.BusinessProfile;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Entity.WaiterProfile;
import com.shabanaj.beloyal.Repository.BusinessProfileRepository;
import com.shabanaj.beloyal.Repository.WaiterProfileRepository;
import com.shabanaj.beloyal.Service.WaiterProfileService;

public class WaiterProfileServiceImpl implements WaiterProfileService {
    private final WaiterProfileRepository waiterProfileRepository;
    private final BusinessProfileRepository businessProfileRepository;

    public WaiterProfileServiceImpl(WaiterProfileRepository waiterProfileRepository, BusinessProfileRepository businessProfileRepository) {
        this.waiterProfileRepository = waiterProfileRepository;
        this.businessProfileRepository = businessProfileRepository;
    }


    @Override
    public WaiterProfile createWaiterProfile(User user,BusinessProfile businessProfile, WaiterProfileRegisterDto dto) {
        WaiterProfile waiterProfile= new WaiterProfile();

        waiterProfile.setUser(user);
        waiterProfile.setEmployeeCode(dto.getEmployeeCode());
        waiterProfile.setHireDate(dto.getHireDate());
        waiterProfile.setBusinessProfile(businessProfile);

        return waiterProfileRepository.save(waiterProfile);
    }
}
