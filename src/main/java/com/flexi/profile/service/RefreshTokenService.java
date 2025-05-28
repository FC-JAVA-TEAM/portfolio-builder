package com.flexi.profile.service;

import com.flexi.profile.model.RefreshToken;
import com.flexi.profile.repository.RefreshTokenRepository;
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

    public RefreshToken createRefreshToken(String userId, String deviceInfo) {
        String family = UUID.randomUUID().toString();
        return createRefreshTokenInFamily(userId, deviceInfo, family);
    }

    private RefreshToken createRefreshTokenInFamily(String userId, String deviceInfo, String family) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setRevoked(false);
        refreshToken.setFamily(family);

        // Revoke all other tokens for this user on this device
        refreshTokenRepository.revokeAllUserTokensOnDevice(userId, deviceInfo, 0L);

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        auditService.logTokenAction("CREATE", userId, savedToken.getToken(), "Created new refresh token");
        return savedToken;
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
        auditService.logTokenAction("ROTATE", oldToken.getUserId(), oldToken.getToken(), "Rotated refresh token");
        // Create new token with new family
        return createRefreshToken(oldToken.getUserId(), oldToken.getDeviceInfo());
    }

    @Transactional
    public void deleteByUserId(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
        auditService.logTokenAction("DELETE_ALL", userId, null, "Deleted all refresh tokens for user");
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
