package com.shabanaj.beloyal.features.pointsBucket.service.impl;

import com.shabanaj.beloyal.features.pointsBucket.repository.PointsBucketRepository;
import com.shabanaj.beloyal.features.pointsBucket.service.PointsBucketService;
import com.shabanaj.beloyal.model.Entity.PointsBucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointsBucketServiceImpl implements PointsBucketService {
    private final PointsBucketRepository pointsBucketRepository;

    @Override
    public PointsBucket save(PointsBucket pointsBucket) {
        return pointsBucketRepository.save(pointsBucket);
    }
}
