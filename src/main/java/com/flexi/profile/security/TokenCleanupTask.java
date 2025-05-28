package com.flexi.profile.security;

import com.flexi.profile.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanupTask {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Scheduled(cron = "0 0 1 * * ?") // Run at 1:00 AM every day
    public void cleanupExpiredTokens() {
        refreshTokenService.cleanupExpiredTokens();
    }
}
