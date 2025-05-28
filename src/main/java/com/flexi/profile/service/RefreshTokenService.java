package com.flexi.profile.service;

import com.flexi.profile.model.RefreshToken;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.RefreshTokenRepository;
import com.flexi.profile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    @Value("${jwt.refresh.expiration.default}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(User user) {
        String family = UUID.randomUUID().toString();
        return createRefreshTokenInFamily(user, "", family);
    }

    public RefreshToken createRefreshToken(String userId, String deviceInfo) {
        String family = UUID.randomUUID().toString();
        return createRefreshTokenInFamily(userId, deviceInfo, family);
    }

    private RefreshToken createRefreshTokenInFamily(User user, String deviceInfo, String family) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setRevoked(false);
        refreshToken.setFamily(family);

        // Revoke all other tokens for this user on this device
        refreshTokenRepository.revokeAllUserTokensOnDevice(user, deviceInfo, 0L);

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        auditService.logTokenAction("CREATE", user.getEmail(), savedToken.getToken(), "Created new refresh token");
        return savedToken;
    }

    private RefreshToken createRefreshTokenInFamily(String userEmail, String deviceInfo, String family) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
            
        return createRefreshTokenInFamily(user, deviceInfo, family);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0 || token.isRevoked()) {
            refreshTokenRepository.delete(token);
            auditService.logTokenAction("EXPIRE", token.getUserId(), token.getToken(), "Token expired or revoked");
            throw new RuntimeException("Refresh token was expired or revoked. Please make a new login request");
        }
        return token;
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        // Invalidate all tokens in the family
        refreshTokenRepository.revokeAllTokensInFamily(oldToken.getFamily());
        User user = oldToken.getUser();
        if (user == null) {
            user = userRepository.findByEmail(oldToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + oldToken.getUserId()));
        }
        auditService.logTokenAction("ROTATE", user.getEmail(), oldToken.getToken(), "Rotated refresh token");
        // Create new token with new family
        String family = UUID.randomUUID().toString();
        return createRefreshTokenInFamily(user, oldToken.getDeviceInfo(), family);
    }

    @Transactional
    public void deleteByUserId(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);
        refreshTokenRepository.deleteAll(tokens);
        auditService.logTokenAction("DELETE_ALL", userEmail, null, "Deleted all refresh tokens for user");
    }

    @Transactional
    public void revokeToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        refreshToken.ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
            auditService.logTokenAction("REVOKE", rt.getUserId(), rt.getToken(), "Revoked refresh token");
        });
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        List<RefreshToken> userTokens = refreshTokenRepository.findByUser(user);
        for (RefreshToken token : userTokens) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            auditService.logTokenAction("REVOKE", user.getEmail(), token.getToken(), "Token revoked during user logout");
        }
    }

    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
        auditService.logTokenAction("CLEANUP", "SYSTEM", null, "Cleaned up expired tokens");
    }

    public boolean isTokenValid(String token) {
        Optional<RefreshToken> refreshToken = findByToken(token);
        return refreshToken.map(rt -> !rt.isRevoked() && rt.getExpiryDate().isAfter(Instant.now()))
                         .orElse(false);
    }

    @Transactional
    public void revokeAllTokens() {
        List<RefreshToken> allTokens = refreshTokenRepository.findAll();
        for (RefreshToken token : allTokens) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            auditService.logTokenAction("REVOKE", token.getUserId(), token.getToken(), "Token revoked in mass revocation");
        }
    }
}
