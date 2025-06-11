package com.flexi.profile.service;

import com.flexi.profile.dto.AuthRequest;
import com.flexi.profile.response.ApiResponseDTO;
import com.flexi.profile.dto.AuthResponse;
import org.springframework.security.core.Authentication;

public interface EnhancedAuthService {
    ApiResponseDTO<AuthResponse> registerUser(AuthRequest authRequest);
    ApiResponseDTO<AuthResponse> loginUser(AuthRequest authRequest);
    ApiResponseDTO<AuthResponse> refreshToken(String refreshToken);
    ApiResponseDTO<Void> logout(String token);
    ApiResponseDTO<AuthResponse> getCurrentUser(Authentication authentication);
}
