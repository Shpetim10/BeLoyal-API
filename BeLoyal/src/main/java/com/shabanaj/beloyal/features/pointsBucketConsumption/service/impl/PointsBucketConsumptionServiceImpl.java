package com.shabanaj.beloyal.features.pointsBucketConsumption.service.impl;

import com.shabanaj.beloyal.features.pointsBucketConsumption.repository.PointsBucketConsumptionRepository;
import com.shabanaj.beloyal.features.pointsBucketConsumption.service.PointsBucketConsumptionService;
import com.shabanaj.beloyal.model.Entity.PointsBucketConsumption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointsBucketConsumptionServiceImpl implements PointsBucketConsumptionService {
    private final PointsBucketConsumptionRepository pointsBucketConsumptionRepository;

    @Override
    public PointsBucketConsumption save(PointsBucketConsumption pointsBucketConsumption) {
        return pointsBucketConsumptionRepository.save(pointsBucketConsumption);
    }
}
