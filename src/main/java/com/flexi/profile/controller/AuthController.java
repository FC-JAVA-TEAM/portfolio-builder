package com.flexi.profile.controller;

import com.flexi.profile.dto.AuthRequest;
import com.flexi.profile.dto.AuthResponse;
import com.flexi.profile.model.RefreshToken;
import com.flexi.profile.model.User;
import com.flexi.profile.service.AuthService;
import com.flexi.profile.service.RefreshTokenService;
import com.flexi.profile.util.LogUtil;
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
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody AuthRequest authRequest) {
        logger.debug("Received registration request for user: {}", authRequest.getEmail());
        LogUtil.logMethodEntry(logger, "registerUser", authRequest);
        ResponseEntity<AuthResponse> response = ResponseEntity.ok(authService.registerUser(authRequest));
        logger.info("User registered successfully: {}", authRequest.getEmail());
        LogUtil.logMethodExit(logger, "registerUser", response);
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest authRequest) {
        logger.debug("Received login request for user: {}", authRequest.getEmail());
        LogUtil.logMethodEntry(logger, "loginUser", authRequest);
        ResponseEntity<AuthResponse> response = ResponseEntity.ok(authService.loginUser(authRequest));
        logger.info("User logged in successfully: {}", authRequest.getEmail());
        LogUtil.logMethodExit(logger, "loginUser", response);
        return response;
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        logger.debug("Received token refresh request");
        LogUtil.logMethodEntry(logger, "refreshToken", request);
        ResponseEntity<AuthResponse> response = ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
        logger.info("Token refreshed successfully");
        LogUtil.logMethodExit(logger, "refreshToken", response);
        return response;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        logger.debug("Received logout request");
        LogUtil.logMethodEntry(logger, "logout", token);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        ResponseEntity<Void> response = ResponseEntity.ok().build();
        logger.info("User logged out successfully");
        LogUtil.logMethodExit(logger, "logout", response);
        return response;
    }

    @GetMapping("/status")
    public ResponseEntity<AuthResponse> checkAuthStatus() {
        logger.debug("Checking authentication status");
        LogUtil.logMethodEntry(logger, "checkAuthStatus");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ResponseEntity<AuthResponse> response;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            response = ResponseEntity.ok(authService.getCurrentUser(authentication));
            logger.info("User is authenticated: {}", authentication.getName());
        } else {
            response = ResponseEntity.status(401).build();
            logger.info("User is not authenticated");
        }
        LogUtil.logMethodExit(logger, "checkAuthStatus", response);
        return response;
    }
}
