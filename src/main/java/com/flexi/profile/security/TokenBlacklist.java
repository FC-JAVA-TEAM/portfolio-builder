package com.flexi.profile.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class TokenBlacklist {
    private final ConcurrentMap<String, Boolean> blacklist = new ConcurrentHashMap<>();

    public void addToBlacklist(String token) {
        blacklist.put(token, true);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }
}
