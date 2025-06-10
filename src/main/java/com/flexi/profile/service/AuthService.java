package com.flexi.profile.service;

import com.flexi.profile.dto.AuthRequest;
import com.flexi.profile.dto.AuthResponse;
import com.flexi.profile.model.User;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthResponse registerUser(AuthRequest authRequest);
    AuthResponse registerUser(AuthRequest authRequest, String roleName);
    AuthResponse loginUser(AuthRequest authRequest);
    AuthResponse getCurrentUser(Authentication authentication);
    String createAccessToken(User user);
    long getAccessTokenExpirationTime();
    long getRefreshTokenExpirationTime();
    void logout(String token);
    AuthResponse refreshToken(String refreshToken);
}
