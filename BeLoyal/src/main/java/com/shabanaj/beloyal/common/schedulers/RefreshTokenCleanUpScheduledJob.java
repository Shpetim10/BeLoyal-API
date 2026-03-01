package com.shabanaj.beloyal.common.schedulers;

import com.shabanaj.beloyal.common.token.service.RefreshTokenCleanUpService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanUpScheduledJob {
    private final RefreshTokenCleanUpService refreshTokenCleanUpService;
    private final Logger logger = LogManager.getLogger(RefreshTokenCleanUpScheduledJob.class);

    @Scheduled(fixedRate = 1 * 60 * 1000)
    public void run(){
        logger.info("Starting refresh token cleanup job");

        // Execute delete query
        refreshTokenCleanUpService.cleanUpRevokedTokens();

        logger.info("Refresh token cleanup job finished");
    }
}
