package com.flexi.profile.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TokenBlacklist {
    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklist.class);
    private final ConcurrentHashMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();

    public TokenBlacklist() {
        logger.info("Initializing TokenBlacklist");
        // Schedule cleanup of expired tokens every hour
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.HOURS);
        logger.debug("Scheduled token cleanup task to run every hour");
    }

    public void addToBlacklist(String token) {
        logger.debug("Adding token to blacklist");
        // Add token with current timestamp
        blacklistedTokens.put(token, System.currentTimeMillis());
        logger.info("Token added to blacklist. Total blacklisted tokens: {}", blacklistedTokens.size());
    }

    public boolean isBlacklisted(String token) {
        boolean isBlacklisted = blacklistedTokens.containsKey(token);
        logger.debug("Checking if token is blacklisted: {}", isBlacklisted);
        return isBlacklisted;
    }

    private void cleanupExpiredTokens() {
        logger.debug("Starting cleanup of expired tokens");
        long now = System.currentTimeMillis();
        int initialSize = blacklistedTokens.size();
        // Remove tokens older than 24 hours
        blacklistedTokens.entrySet().removeIf(entry -> {
            boolean shouldRemove = (now - entry.getValue()) > TimeUnit.HOURS.toMillis(24);
            if (shouldRemove) {
                logger.debug("Removing expired token from blacklist");
            }
            return shouldRemove;
        });
        int removedCount = initialSize - blacklistedTokens.size();
        logger.info("Cleanup completed. Removed {} expired tokens. Remaining blacklisted tokens: {}", removedCount, blacklistedTokens.size());
    }
}
