package com.flexi.profile.controller;

import com.flexi.profile.dto.AuthRequest;
import com.flexi.profile.dto.AuthResponse;
import com.flexi.profile.model.RefreshToken;
import com.flexi.profile.security.TokenBlacklist;
import com.flexi.profile.service.AuthService;
import com.flexi.profile.service.RefreshTokenService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:1111")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    String accessToken = authService.createAccessToken(userId);
                    return ResponseEntity.ok(new AuthResponse(
                        accessToken,
                        requestRefreshToken,
                        null,
                        null,
                        userId,
                        authService.getAccessTokenExpirationTime(),
                        authService.getRefreshTokenExpirationTime()
                    ));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest authRequest) {
        try {
            AuthResponse response = authService.registerUser(authRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during user registration: ", e);
            return ResponseEntity.status(500).body("Error during registration: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.loginUser(authRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<AuthResponse> checkAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user is authenticated and not anonymous
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            try {
                AuthResponse response = authService.getCurrentUser(authentication);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                // Log the error but return 401 to maintain consistency
                System.err.println("Error getting current user: " + e.getMessage());
                return ResponseEntity.status(401).build();
            }
        }
        
        // Return 401 for unauthenticated or anonymous users
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token != null) {
            tokenBlacklist.addToBlacklist(token);
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
