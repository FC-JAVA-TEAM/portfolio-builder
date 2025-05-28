package com.flexi.profile.service;

import com.flexi.profile.dto.AuthRequest;
import com.flexi.profile.dto.AuthResponse;
import com.flexi.profile.model.Profile;
import com.flexi.profile.model.RefreshToken;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.ProfileRepository;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final String TEST_USER_EMAIL = "test@example.com";
    private static final String TEST_USER_PASSWORD = "password123";

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

    @PostConstruct
    public void init() {
        createTestUserIfNotExist();
    }

    @Override
    public void logout(String token) {
        jwtTokenProvider.invalidateToken(token);
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenService.revokeAllUserTokens(user);
        logger.info("User logged out: {}", email);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = createAccessToken(user);
                    refreshTokenService.revokeAllUserTokens(user);
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                    return new AuthResponse(
                        accessToken,
                        newRefreshToken.getToken(),
                        String.valueOf(user.getId()),
                        user.getFirstName(),
                        user.getEmail(),
                        jwtTokenProvider.getAccessTokenValidityInMilliseconds(isAdmin(user)),
                        jwtTokenProvider.getRefreshTokenValidityInMilliseconds(isAdmin(user))
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token not found in database"));
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenService.revokeAllUserTokens(token.getUser());
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    private void createTestUserIfNotExist() {
        if (!userRepository.existsByEmail(TEST_USER_EMAIL)) {
            AuthRequest testUser = new AuthRequest();
            testUser.setEmail(TEST_USER_EMAIL);
            testUser.setPassword(TEST_USER_PASSWORD);
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            registerUser(testUser);
            logger.info("Test user created: {}", TEST_USER_EMAIL);
        }
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getRole().equals("ROLE_ADMIN"));
    }

    @Override
    public String createAccessToken(User user) {
        return jwtTokenProvider.createAccessToken(user.getEmail(), isAdmin(user));
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
        if (userRepository.existsByEmail(authRequest.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setFirstName(authRequest.getFirstName());
        user.setLastName(authRequest.getLastName());
        user = userRepository.save(user);

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setName(authRequest.getFirstName() + " " + authRequest.getLastName());
        profile.setBio("");
        profile.setIsPublic(false);
        profileRepository.save(profile);

        logger.info("New user registered: {}", user.getEmail());

        boolean isAdmin = isAdmin(user);
        String accessToken = createAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
            accessToken,
            refreshToken.getToken(),
            String.valueOf(user.getId()),
            user.getFirstName(),
            user.getEmail(),
            jwtTokenProvider.getAccessTokenValidityInMilliseconds(isAdmin),
            jwtTokenProvider.getRefreshTokenValidityInMilliseconds(isAdmin)
        );
    }

    @Override
    public AuthResponse loginUser(AuthRequest authRequest) {
        logger.info("Login attempt for user: {}", authRequest.getEmail());

        User user = userRepository.findByEmail(authRequest.getEmail())
            .orElseThrow(() -> {
                logger.warn("Login failed: User not found for email: {}", authRequest.getEmail());
                return new RuntimeException("Invalid credentials");
            });

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            logger.warn("Login failed: Invalid password for user: {}", authRequest.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        logger.info("User logged in successfully: {}", user.getEmail());

        boolean isAdmin = isAdmin(user);
        String accessToken = createAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
            accessToken,
            refreshToken.getToken(),
            String.valueOf(user.getId()),
            user.getFirstName(),
            user.getEmail(),
            jwtTokenProvider.getAccessTokenValidityInMilliseconds(isAdmin),
            jwtTokenProvider.getRefreshTokenValidityInMilliseconds(isAdmin)
        );
    }

    @Override
    public AuthResponse getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        logger.info("Getting current user for: {}", email);

        if ("anonymousUser".equals(email)) {
            logger.info("Anonymous user detected, returning null");
            return null;
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.warn("User not found for authenticated user: {}", email);
                return new RuntimeException("User not found");
            });

        boolean isAdmin = isAdmin(user);
        String accessToken = createAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        logger.info("Successfully retrieved user profile for: {}", email);

        return new AuthResponse(
            accessToken,
            refreshToken.getToken(),
            String.valueOf(user.getId()),
            user.getFirstName(),
            user.getEmail(),
            jwtTokenProvider.getAccessTokenValidityInMilliseconds(isAdmin),
            jwtTokenProvider.getRefreshTokenValidityInMilliseconds(isAdmin)
        );
    }
}
