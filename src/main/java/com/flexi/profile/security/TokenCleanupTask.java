package com.flexi.profile.security;

import com.flexi.profile.service.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanupTask {
    private static final Logger logger = LoggerFactory.getLogger(TokenCleanupTask.class);

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Scheduled(cron = "0 0 1 * * ?") // Run at 1:00 AM every day
    public void cleanupExpiredTokens() {
        logger.debug("Starting scheduled token cleanup task");
        try {
            refreshTokenService.cleanupExpiredTokens();
            logger.info("Successfully completed scheduled token cleanup task");
        } catch (Exception e) {
            logger.error("Error during scheduled token cleanup task", e);
            throw e;
        }
    }
}
