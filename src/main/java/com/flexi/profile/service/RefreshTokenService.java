package com.flexi.profile.service;

import com.flexi.profile.model.RefreshToken;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.RefreshTokenRepository;
import com.flexi.profile.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    @Value("${jwt.refresh.expiration.default}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(User user) {
        logger.debug("Creating refresh token for user: {}", user.getEmail());
        String family = UUID.randomUUID().toString();
        return createRefreshTokenInFamily(user, "", family);
    }

    public RefreshToken createRefreshToken(Long userId, String deviceInfo) {
        String family = UUID.randomUUID().toString();
        return createRefreshTokenInFamily(userId, deviceInfo, family);
    }

    private RefreshToken createRefreshTokenInFamily(User user, String deviceInfo, String family) {
        logger.debug("Creating refresh token in family {} for user: {}", family, user.getEmail());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setRevoked(false);
        refreshToken.setFamily(family);

        logger.debug("Revoking all existing tokens for user {} on device {}", user.getEmail(), deviceInfo);
        refreshTokenRepository.revokeAllUserTokensOnDevice(user, deviceInfo, 0L);

        logger.debug("Saving new refresh token to database");
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        logger.info("Created new refresh token for user: {}", user.getEmail());
        auditService.logTokenAction("CREATE", user.getId(), savedToken.getId(), "Created new refresh token");
        return savedToken;
    }

    private RefreshToken createRefreshTokenInFamily(Long userId, String deviceInfo, String family) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
        return createRefreshTokenInFamily(user, deviceInfo, family);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        logger.debug("Verifying expiration for token: {}", token.getToken());
        if (token.getExpiryDate().compareTo(Instant.now()) < 0 || token.isRevoked()) {
            logger.info("Token expired or revoked for user: {}", token.getUser().getId());
            refreshTokenRepository.delete(token);
            auditService.logTokenAction("EXPIRE", token.getUser().getId(), token.getId(), "Token expired or revoked");
            throw new RuntimeException("Refresh token was expired or revoked. Please make a new login request");
        }
        logger.debug("Token is valid and not expired");
        return token;
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        logger.debug("Rotating refresh token for family: {}", oldToken.getFamily());
        
        logger.debug("Revoking all tokens in family: {}", oldToken.getFamily());
        refreshTokenRepository.revokeAllTokensInFamily(oldToken.getFamily());
        
        User user = oldToken.getUser();
        if (user == null) {
            logger.debug("User object not found in token, fetching by id: {}", oldToken.getUser().getId());
            user = userRepository.findById(oldToken.getUser().getId())
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", oldToken.getUser().getId());
                    return new RuntimeException("User not found with id: " + oldToken.getUser().getId());
                });
        }
        
        auditService.logTokenAction("ROTATE", user.getId(), oldToken.getId(), "Rotated refresh token");
        
        String family = UUID.randomUUID().toString();
        logger.info("Rotating token for user: {} with new family: {}", user.getEmail(), family);
        return createRefreshTokenInFamily(user, oldToken.getDeviceInfo(), family);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);
        refreshTokenRepository.deleteAll(tokens);
        auditService.logTokenAction("DELETE_ALL", userId, null, "Deleted all refresh tokens for user");
    }

    @Transactional
    public void revokeToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        refreshToken.ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
            auditService.logTokenAction("REVOKE", rt.getUser().getId(), rt.getId(), "Revoked refresh token");
        });
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        logger.debug("Revoking all tokens for user: {}", user.getEmail());
        List<RefreshToken> userTokens = refreshTokenRepository.findByUser(user);
        logger.debug("Found {} tokens to revoke", userTokens.size());
        
        for (RefreshToken token : userTokens) {
            logger.debug("Revoking token: {}", token.getToken());
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            auditService.logTokenAction("REVOKE", user.getId(), token.getId(), "Token revoked during user logout");
        }
        
        logger.info("Successfully revoked {} tokens for user: {}", userTokens.size(), user.getEmail());
    }

    @Transactional
    public void cleanupExpiredTokens() {
        logger.debug("Starting cleanup of expired tokens");
        refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
        logger.info("Cleaned up expired tokens");
        auditService.logTokenAction("CLEANUP", 0L, null, "Cleaned up expired tokens");
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
            auditService.logTokenAction("REVOKE", token.getUser().getId(), token.getId(), "Token revoked in mass revocation");
        }
    }
}
