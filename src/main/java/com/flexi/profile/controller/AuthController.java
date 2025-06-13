package com.flexi.profile.controller;

import com.flexi.profile.dto.AuthRequest;
import com.flexi.profile.dto.AuthResponse;
import com.flexi.profile.dto.TokenRefreshRequest;
import com.flexi.profile.exception.auth.TokenException;
import com.flexi.profile.response.ApiResponseDTO;
import com.flexi.profile.service.EnhancedAuthService;
import com.flexi.profile.service.EnhancedAuthServiceImpl;
import com.flexi.profile.util.LogUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private EnhancedAuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<AuthResponse>> registerUser(@Valid @RequestBody AuthRequest authRequest) {
        logger.debug("Received registration request for user: {}", authRequest.getEmail());
        LogUtil.logMethodEntry(logger, "registerUser", authRequest);
        ApiResponseDTO<AuthResponse> response = authService.registerUser(authRequest);
        LogUtil.logMethodExit(logger, "registerUser", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponse>> loginUser( @RequestBody AuthRequest authRequest) {
        logger.debug("Received login request for user: {}", authRequest.getEmail());
        LogUtil.logMethodEntry(logger, "loginUser", authRequest);
        ApiResponseDTO<AuthResponse> response = authService.loginUser(authRequest);
        LogUtil.logMethodExit(logger, "loginUser", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDTO<AuthResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        logger.debug("Received token refresh request");
        LogUtil.logMethodEntry(logger, "refreshToken", request);
        ApiResponseDTO<AuthResponse> response = authService.refreshToken(request.getRefreshToken());
        LogUtil.logMethodExit(logger, "refreshToken", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<Void>> logout(@RequestHeader("Authorization") String token) {
        logger.debug("Received logout request");
        LogUtil.logMethodEntry(logger, "logout", token);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        ApiResponseDTO<Void> response = authService.logout(token);
        LogUtil.logMethodExit(logger, "logout", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponseDTO<AuthResponse>> checkAuthStatus() {
        logger.debug("Checking authentication status");
        LogUtil.logMethodEntry(logger, "checkAuthStatus");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            ApiResponseDTO<AuthResponse> response = authService.getCurrentUser(authentication);
            logger.info("User is authenticated: {}", authentication.getName());
            LogUtil.logMethodExit(logger, "checkAuthStatus", response);
            return ResponseEntity.ok(response);
        } catch (TokenException e) {
            logger.info("User is not authenticated: {}", e.getMessage());
            LogUtil.logMethodExit(logger, "checkAuthStatus", e.getMessage());
            return ResponseEntity.ok(ApiResponseDTO.error(e.getMessage()));
        }
    }
}
