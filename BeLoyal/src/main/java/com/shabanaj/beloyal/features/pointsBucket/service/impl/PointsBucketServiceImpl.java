package com.shabanaj.beloyal.features.pointsBucket.service.impl;

import com.shabanaj.beloyal.features.loyaltySettings.service.LoyaltySettingsService;
import com.shabanaj.beloyal.features.pointsBucket.repository.PointsBucketRepository;
import com.shabanaj.beloyal.features.pointsBucket.service.PointsBucketService;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionService;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import com.shabanaj.beloyal.model.Entity.LoyaltySettings;
import com.shabanaj.beloyal.model.Entity.PointsBucket;
import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import com.shabanaj.beloyal.model.Enums.PointsBucketStatus;
import com.shabanaj.beloyal.model.Enums.PointsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PointsBucketServiceImpl implements PointsBucketService {
    private final PointsBucketRepository pointsBucketRepository;
    private final LoyaltySettingsService loyaltySettingsService;
    private final PointsTransactionService pointsTransactionService;

    @Override
    public PointsBucket save(PointsBucket pointsBucket) {
        return pointsBucketRepository.save(pointsBucket);
    }

    @Override
    @Transactional
    public void expireBuckets() {
        // Find all
        try(Stream<PointsBucket> pointsBuckets = pointsBucketRepository.streamExpiredPointsBuckets()){
            pointsBuckets.forEach(pointsBucket -> {
                // update loyalty account points
                LoyaltyAccount loyaltyAccount = pointsBucket.getLoyaltyAccount();
                loyaltyAccount.expire(pointsBucket.getPointsRemaining());

                // mark points bucket as expired
                pointsBucket.setStatus(PointsBucketStatus.EXPIRED);

                // loyalty settings
                LoyaltySettings loyaltySettings= loyaltySettingsService.getLoyaltySettings(loyaltyAccount.getBusiness().getId());

                // create expiration transaction
                PointsTransaction pointsTransaction= PointsTransaction.builder()
                        .loyaltyAccount(loyaltyAccount)
                        .billTransaction(pointsBucket.getSourceTransaction().getBillTransaction())
                        .businessMember(null)
                        .type(PointsType.EXPIRE)
                        .description("This points are expired and can no longer be used. This points were created on "+
                                pointsBucket.getEarnedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))+
                                " and expired on "+
                                pointsBucket.getExpiresAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))+". ")
                        .reason("Points get expired after" +
                                loyaltySettings.getMonthsToExpire()
                                +" months stated in the business loyalty settings. ")
                        .pointsDelta(pointsBucket.getPointsRemaining()* (-1))
                        .build();

                pointsTransactionService.save(pointsTransaction);
            });
        }
    }
}
