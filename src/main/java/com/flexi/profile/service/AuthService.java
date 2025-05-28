package com.flexi.profile.service;

import com.flexi.profile.dto.AuthRequest;
import com.flexi.profile.dto.AuthResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthResponse registerUser(AuthRequest authRequest);
    AuthResponse loginUser(AuthRequest authRequest);
    AuthResponse getCurrentUser(Authentication authentication);
    String createAccessToken(String userId);
    long getAccessTokenExpirationTime();
    long getRefreshTokenExpirationTime();
}
