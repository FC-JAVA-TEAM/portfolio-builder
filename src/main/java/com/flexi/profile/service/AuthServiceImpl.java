package com.flexi.profile.service;

import com.flexi.profile.dto.AuthRequest;
import com.flexi.profile.dto.AuthResponse;
import com.flexi.profile.model.Profile;
import com.flexi.profile.repository.ProfileRepository;
import com.flexi.profile.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final String TEST_USER_EMAIL = "test@example.com";
    private static final String TEST_USER_PASSWORD = "password123";

    @PostConstruct
    public void init() {
        createTestUserIfNotExist();
    }

    private void createTestUserIfNotExist() {
        if (profileRepository.findByUserId(TEST_USER_EMAIL).isEmpty()) {
            AuthRequest testUser = new AuthRequest();
            testUser.setEmail(TEST_USER_EMAIL);
            testUser.setPassword(TEST_USER_PASSWORD);
            testUser.setName("Test User");
            registerUser(testUser);
            logger.info("Test user created: {}", TEST_USER_EMAIL);
        }
    }

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse registerUser(AuthRequest authRequest) {
        // Check if user already exists
        if (!profileRepository.findByUserId(authRequest.getEmail()).isEmpty()) {
            throw new RuntimeException("User already exists");
        }

        // Create new profile
        Profile profile = new Profile();
        profile.setUserId(authRequest.getEmail());
        profile.setName(authRequest.getName());
        profile.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        profile.setBio("");
        profile.setIsPublic(false);
        
        // Save profile
        Profile savedProfile = profileRepository.save(profile);
        logger.info("New user registered: {}", savedProfile.getUserId());
        
        // Generate JWT token
        String token = jwtTokenProvider.createToken(savedProfile.getUserId());

        // Create response
        return new AuthResponse(
            token,
            String.valueOf(savedProfile.getId()),
            savedProfile.getName(),
            savedProfile.getUserId()
        );
    }

    @Override
    public AuthResponse loginUser(AuthRequest authRequest) {
        logger.info("Login attempt for user: {}", authRequest.getEmail());

        // Find user by email
        List<Profile> profiles = profileRepository.findByUserId(authRequest.getEmail());
        
        if (profiles.isEmpty()) {
            logger.warn("Login failed: User not found for email: {}", authRequest.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        Profile profile = profiles.get(0);
        
        // Verify password
        if (!passwordEncoder.matches(authRequest.getPassword(), profile.getPassword())) {
            logger.warn("Login failed: Invalid password for user: {}", authRequest.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        logger.info("User logged in successfully: {}", profile.getUserId());
        
        // Generate JWT token
        String token = jwtTokenProvider.createToken(profile.getUserId());

        // Create response
        return new AuthResponse(
            token,
            String.valueOf(profile.getId()),
            profile.getName(),
            profile.getUserId()
        );
    }

    @Override
    public AuthResponse getCurrentUser(Authentication authentication) {
        String userId = authentication.getName();
        logger.info("Getting current user for: {}", userId);
        
        if ("anonymousUser".equals(userId)) {
            logger.info("Anonymous user detected, returning null");
            return null;
        }
        
        List<Profile> profiles = profileRepository.findByUserId(userId);
        
        if (profiles.isEmpty()) {
            logger.warn("User not found for authenticated user: {}", userId);
            return null;
        }

        Profile profile = profiles.get(0);
        String token = jwtTokenProvider.createToken(profile.getUserId());
        logger.info("Successfully retrieved user profile for: {}", userId);

        return new AuthResponse(
            token,
            String.valueOf(profile.getId()),
            profile.getName(),
            profile.getUserId()
        );
    }
}
