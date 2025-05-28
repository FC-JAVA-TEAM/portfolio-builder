package com.flexi.profile.service;

import com.flexi.profile.dto.AuthRequest;
import com.flexi.profile.dto.AuthResponse;
import com.flexi.profile.model.Profile;
import com.flexi.profile.model.RefreshToken;
import com.flexi.profile.model.User;
import com.flexi.profile.model.UserRole;
import com.flexi.profile.repository.ProfileRepository;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.security.JwtTokenProvider;
import com.flexi.profile.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Override
    public void logout(String token) {
        LogUtil.logMethodEntry(logger, "logout", token);
        try {
            logger.debug("Invalidating token");
            jwtTokenProvider.invalidateToken(token);
            String email = jwtTokenProvider.getEmailFromToken(token);
            logger.debug("Fetching user for email: {}", email);
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            logger.debug("Revoking all refresh tokens for user: {}", email);
            refreshTokenService.revokeAllUserTokens(user);
            logger.info("Successfully logged out user: {}", email);
            LogUtil.logMethodExit(logger, "logout");
        } catch (Exception e) {
            logger.error("Error during logout for token: {}", token, e);
            LogUtil.logError(logger, "Error during logout", e);
            throw e;
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        LogUtil.logMethodEntry(logger, "refreshToken", refreshToken);
        try {
            logger.debug("Finding refresh token in database");
            AuthResponse response = refreshTokenService.findByToken(refreshToken)
                    .map(token -> {
                        logger.debug("Found refresh token for user: {}", token.getUser().getEmail());
                        return verifyExpiration(token);
                    })
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        logger.debug("Creating new access token for user: {}", user.getEmail());
                        String accessToken = createAccessToken(user);
                        logger.debug("Revoking all existing refresh tokens for user: {}", user.getEmail());
                        refreshTokenService.revokeAllUserTokens(user);
                        logger.debug("Creating new refresh token for user: {}", user.getEmail());
                        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                        boolean isAdminUser = isAdmin(user);
                        logger.debug("User {} is admin: {}", user.getEmail(), isAdminUser);
                        return new AuthResponse(
                            accessToken,
                            newRefreshToken.getToken(),
                            String.valueOf(user.getId()),
                            user.getFirstName(),
                            user.getEmail(),
                            jwtTokenProvider.getAccessTokenValidityInMilliseconds(isAdminUser),
                            jwtTokenProvider.getRefreshTokenValidityInMilliseconds(isAdminUser)
                        );
                    })
                    .orElseThrow(() -> {
                        logger.warn("Refresh token not found in database: {}", refreshToken);
                        return new RuntimeException("Refresh token not found in database");
                    });
            logger.info("Successfully refreshed token for user: {}", response.getUserId());
            LogUtil.logMethodExit(logger, "refreshToken", response);
            return response;
        } catch (Exception e) {
            logger.error("Error during token refresh for token: {}", refreshToken, e);
            LogUtil.logError(logger, "Error during token refresh", e);
            throw e;
        }
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        LogUtil.logMethodEntry(logger, "verifyExpiration", token);
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            LogUtil.logInfo(logger, "Refresh token expired for user: " + token.getUser().getEmail());
            refreshTokenService.revokeAllUserTokens(token.getUser());
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        LogUtil.logMethodExit(logger, "verifyExpiration", token);
        return token;
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getRole().equals("ROLE_ADMIN"));
    }

    @Override
    public String createAccessToken(User user) {
        LogUtil.logMethodEntry(logger, "createAccessToken", user.getEmail());
        String token = jwtTokenProvider.createAccessToken(user.getEmail(), isAdmin(user));
        LogUtil.logMethodExit(logger, "createAccessToken", token);
        return token;
    }

    @Override
    public long getAccessTokenExpirationTime() {
        return jwtTokenProvider.getAccessTokenValidityInMilliseconds(false);
    }

    @Override
    public long getRefreshTokenExpirationTime() {
        return jwtTokenProvider.getRefreshTokenValidityInMilliseconds(false);
    }

    @Override
    public AuthResponse registerUser(AuthRequest authRequest) {
        LogUtil.logMethodEntry(logger, "registerUser", authRequest);
        try {
            logger.debug("Checking if user already exists: {}", authRequest.getEmail());
            if (userRepository.existsByEmail(authRequest.getEmail())) {
                logger.info("Registration failed: User already exists: {}", authRequest.getEmail());
                throw new RuntimeException("User already exists");
            }

            logger.debug("Creating new user: {}", authRequest.getEmail());
            User user = new User();
            user.setEmail(authRequest.getEmail());
            user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
            user.setFirstName(authRequest.getFirstName());
            user.setLastName(authRequest.getLastName());
            
            logger.debug("Adding default role to user: {}", authRequest.getEmail());
            UserRole defaultRole = new UserRole(user, User.DEFAULT_ROLE);
            user.addRole(defaultRole);
            
            user = userRepository.save(user);
            logger.info("Created new user: {}", user.getEmail());

            logger.debug("Creating initial profile for user: {}", user.getEmail());
            Profile profile = new Profile();
            profile.setName(authRequest.getFirstName() + " " + authRequest.getLastName());
            profile.setBio("");
            profile.setIsPublic(false);
            user.addProfile(profile);
            profileRepository.save(profile);
            logger.info("Created initial profile for user: {}", user.getEmail());

            boolean isAdmin = isAdmin(user);
            logger.debug("User {} is admin: {}", user.getEmail(), isAdmin);
            
            logger.debug("Creating access token for user: {}", user.getEmail());
            String accessToken = createAccessToken(user);
            
            logger.debug("Creating refresh token for user: {}", user.getEmail());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            AuthResponse response = new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                String.valueOf(user.getId()),
                user.getFirstName(),
                user.getEmail(),
                jwtTokenProvider.getAccessTokenValidityInMilliseconds(isAdmin),
                jwtTokenProvider.getRefreshTokenValidityInMilliseconds(isAdmin)
            );
            logger.info("User registered successfully: {}", user.getEmail());
            LogUtil.logMethodExit(logger, "registerUser", response);
            return response;
        } catch (Exception e) {
            logger.error("Error during user registration for email: {}", authRequest.getEmail(), e);
            LogUtil.logError(logger, "Error during user registration", e);
            throw e;
        }
    }

    @Override
    public AuthResponse loginUser(AuthRequest authRequest) {
        LogUtil.logMethodEntry(logger, "loginUser", authRequest);
        try {
            User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> {
                    LogUtil.logInfo(logger, "Login failed: User not found: " + authRequest.getEmail());
                    return new RuntimeException("Invalid credentials");
                });

            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                LogUtil.logInfo(logger, "Login failed: Invalid password for user: " + authRequest.getEmail());
                throw new RuntimeException("Invalid credentials");
            }

            LogUtil.logInfo(logger, "User logged in successfully: " + user.getEmail());

            boolean isAdmin = isAdmin(user);
            String accessToken = createAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            AuthResponse response = new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                String.valueOf(user.getId()),
                user.getFirstName(),
                user.getEmail(),
                jwtTokenProvider.getAccessTokenValidityInMilliseconds(isAdmin),
                jwtTokenProvider.getRefreshTokenValidityInMilliseconds(isAdmin)
            );
            LogUtil.logMethodExit(logger, "loginUser", response);
            return response;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error during user login", e);
            throw e;
        }
    }

    @Override
    public AuthResponse getCurrentUser(Authentication authentication) {
        LogUtil.logMethodEntry(logger, "getCurrentUser", authentication);
        try {
            String email = authentication.getName();
            if ("anonymousUser".equals(email)) {
                LogUtil.logInfo(logger, "Anonymous user detected");
                return null;
            }

            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LogUtil.logWarning(logger, "User not found for authenticated user: " + email);
                    return new RuntimeException("User not found");
                });

            boolean isAdmin = isAdmin(user);
            String accessToken = createAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            AuthResponse response = new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                String.valueOf(user.getId()),
                user.getFirstName(),
                user.getEmail(),
                jwtTokenProvider.getAccessTokenValidityInMilliseconds(isAdmin),
                jwtTokenProvider.getRefreshTokenValidityInMilliseconds(isAdmin)
            );
            LogUtil.logMethodExit(logger, "getCurrentUser", response);
            return response;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error getting current user", e);
            throw e;
        }
    }
}
