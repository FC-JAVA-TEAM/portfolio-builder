package com.flexi.profile.security;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TokenBlacklist {
    private final ConcurrentHashMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();

    public TokenBlacklist() {
        // Schedule cleanup of expired tokens every hour
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.HOURS);
    }

    public void addToBlacklist(String token) {
        // Add token with current timestamp
        blacklistedTokens.put(token, System.currentTimeMillis());
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    private void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        // Remove tokens older than 24 hours
        blacklistedTokens.entrySet().removeIf(entry -> (now - entry.getValue()) > TimeUnit.HOURS.toMillis(24));
    }
}
