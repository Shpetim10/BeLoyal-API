package com.shabanaj.beloyal.features.pointsBucket.schedulers;

import com.shabanaj.beloyal.features.pointsBucket.service.PointsBucketService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpirePointsBucketScheduler {
    private final PointsBucketService pointsBucketService;
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Scheduled(cron = "0 0 2 * * *")
    public void run(){
        logger.info("Running PointsBucketScheduler");

        pointsBucketService.expireBuckets();

        logger.info("Finished running PointsBucketScheduler");
    }
}
