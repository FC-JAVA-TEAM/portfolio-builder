package com.flexi.profile.service;

import com.flexi.profile.dto.AuthRequest;
import com.flexi.profile.dto.AuthResponse;
import com.flexi.profile.exception.UnauthorizedException;
import com.flexi.profile.exception.ResourceNotFoundException;
import com.flexi.profile.exception.auth.InvalidLoginException;
import com.flexi.profile.exception.auth.RegistrationException;
import com.flexi.profile.exception.auth.TokenException;
import com.flexi.profile.exception.service.auth.UserAlreadyExistsException;
import com.flexi.profile.model.Profile;
import com.flexi.profile.model.RefreshToken;
import com.flexi.profile.model.Role;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.ProfileRepository;
import com.flexi.profile.repository.RoleRepository;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.response.ApiResponseDTO;
import com.flexi.profile.security.JwtTokenProvider;
import com.flexi.profile.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class EnhancedAuthServiceImpl implements EnhancedAuthService {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedAuthServiceImpl.class);

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

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public ApiResponseDTO<Void> logout(String token) {
        LogUtil.logMethodEntry(logger, "logout", token);
        try {
            jwtTokenProvider.invalidateToken(token);
            String email = jwtTokenProvider.getEmailFromToken(token);
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new TokenException("Invalid token: user not found"));
            refreshTokenService.revokeAllUserTokens(user);
            logger.info("Successfully logged out user: {}", email);
            return ApiResponseDTO.success("Logged out successfully", null);
        } catch (TokenException e) {
            logger.warn("Logout failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error during logout for token: {}", token, e);
            throw new TokenException("An error occurred during logout");
        }
    }

    @Override
    public ApiResponseDTO<AuthResponse> refreshToken(String refreshToken) {
        LogUtil.logMethodEntry(logger, "refreshToken", refreshToken);
        try {
            return refreshTokenService.findByToken(refreshToken)
                    .map(this::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), isAdmin(user));
                        refreshTokenService.revokeAllUserTokens(user);
                        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                        AuthResponse response = createAuthResponse(user, accessToken, newRefreshToken.getToken());
                        return ApiResponseDTO.success("Token refreshed successfully", response);
                    })
                    .orElseThrow(() -> new TokenException("Refresh token not found"));
        } catch (TokenException e) {
            logger.warn("Token refresh failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error during token refresh for token: {}", refreshToken, e);
            throw new TokenException("An error occurred while refreshing the token");
        }
    }

    @Override
    public ApiResponseDTO<AuthResponse> registerUser(AuthRequest authRequest) {
        LogUtil.logMethodEntry(logger, "registerUser", authRequest);
        try {
            if (userRepository.existsByEmail(authRequest.getEmail())) {
                throw new RegistrationException("Email is already registered");
            }

            User user = new User();
            user.setEmail(authRequest.getEmail());
            user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
            user.setFirstName(authRequest.getFirstName());
            user.setLastName(authRequest.getLastName());
            
            Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: ROLE_USER"));
            user.addRole(role);
            
            user = userRepository.save(user);

            Profile profile = new Profile();
            profile.setName(authRequest.getFirstName() + " " + authRequest.getLastName());
            profile.setBio("");
            profile.setIsPublic(false);
            user.addProfile(profile);
            profileRepository.save(profile);

            AuthResponse response = AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .build();

            return ApiResponseDTO.success("User registered successfully", response);
        } catch (UserAlreadyExistsException e) {
            logger.warn("Registration failed for email: {}", authRequest.getEmail());
            throw e;
        } catch (Exception e) {
            logger.error("Error during user registration for email: {}", authRequest.getEmail(), e);
            throw new RegistrationException("An error occurred during registration");
        }
    }

    @Override
    public ApiResponseDTO<AuthResponse> loginUser(AuthRequest authRequest) {
        LogUtil.logMethodEntry(logger, "loginUser", authRequest);
        try {
            User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new InvalidLoginException("Invalid email or password"));

            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                throw new InvalidLoginException("Invalid email or password");
            }

            String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), isAdmin(user));
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            AuthResponse response = createAuthResponse(user, accessToken, refreshToken.getToken());
            return ApiResponseDTO.success("User logged in successfully", response);
        } catch (InvalidLoginException e) {
            logger.warn("Login failed for email: {}", authRequest.getEmail());
            throw e;
        } catch (Exception e) {
            logger.error("Error during user login for email: {}", authRequest.getEmail(), e);
            throw new InvalidLoginException("An error occurred during login");
        }
    }

    @Override
    public ApiResponseDTO<AuthResponse> getCurrentUser(Authentication authentication) {
        LogUtil.logMethodEntry(logger, "getCurrentUser", authentication);
        try {
            String email = authentication.getName();
            if ("anonymousUser".equals(email)) {
                throw new TokenException("No authenticated user found");
            }

            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new TokenException("Invalid token: user not found"));

            String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), isAdmin(user));
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            AuthResponse response = createAuthResponse(user, accessToken, refreshToken.getToken());
            return ApiResponseDTO.success("Current user retrieved successfully", response);
        } catch (TokenException e) {
            logger.warn("Authentication check failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error getting current user", e);
            throw new TokenException("An error occurred while checking authentication status");
        }
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(java.time.Instant.now()) < 0) {
            refreshTokenService.revokeAllUserTokens(token.getUser());
            throw new TokenException("Refresh token has expired. Please sign in again");
        }
        return token;
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    private AuthResponse createAuthResponse(User user, String accessToken, String refreshToken) {
        boolean isAdmin = isAdmin(user);
        return AuthResponse.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
