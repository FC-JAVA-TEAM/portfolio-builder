package com.flexi.profile.security;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.flexi.profile.model.User;
import com.flexi.profile.model.Role;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.exception.ResourceNotFoundException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration.default}")
    private long defaultAccessTokenValidityInMilliseconds;

    @Value("${jwt.access.expiration.admin}")
    private long adminAccessTokenValidityInMilliseconds;

    @Value("${jwt.refresh.expiration.default}")
    private long defaultRefreshTokenValidityInMilliseconds;

    @Value("${jwt.refresh.expiration.admin}")
    private long adminRefreshTokenValidityInMilliseconds;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    protected void init() {
        logger.debug("Initializing JWT secret key");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        logger.info("JWT provider initialized successfully");
    }

    public String createAccessToken(String username, boolean isAdmin) {
        logger.debug("Creating access token for user: {}, isAdmin: {}", username, isAdmin);
        long validity = isAdmin ? adminAccessTokenValidityInMilliseconds : defaultAccessTokenValidityInMilliseconds;
        String token = createToken(username, validity);
        logger.info("Created access token for user: {}", username);
        return token;
    }

    public String createRefreshToken(String username, boolean isAdmin) {
        logger.debug("Creating refresh token for user: {}, isAdmin: {}", username, isAdmin);
        long validity = isAdmin ? adminRefreshTokenValidityInMilliseconds : defaultRefreshTokenValidityInMilliseconds;
        String token = createToken(username, validity);
        logger.info("Created refresh token for user: {}", username);
        return token;
    }

    private String createToken(String username, long validityInMilliseconds) {
        logger.debug("Creating token for user: {} with validity: {}ms", username, validityInMilliseconds);
        Claims claims = Jwts.claims().setSubject(username);
        
        // Get user's roles from database
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        List<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .toList();
        claims.put("roles", roles);
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        
        logger.debug("Token created successfully with roles: {}", roles);
        return token;
    }

    public String getUsername(String token) {
        logger.debug("Extracting username from token");
        String username = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        logger.debug("Username extracted from token: {}", username);
        return username;
    }

    public boolean validateToken(String token) {
        logger.debug("Validating token");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            boolean isExpired = claims.getBody().getExpiration().before(new Date());
            boolean isBlacklisted = tokenBlacklist.isBlacklisted(token);
            
            if (isExpired) {
                logger.info("Token validation failed: token is expired");
                return false;
            }
            if (isBlacklisted) {
                logger.info("Token validation failed: token is blacklisted");
                return false;
            }
            
            logger.debug("Token validated successfully");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        logger.debug("Creating authentication from token");
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        String username = claims.getSubject();
        
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        List<SimpleGrantedAuthority> authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .toList();
        
        Authentication auth = new UsernamePasswordAuthenticationToken(username, "", authorities);
        logger.debug("Authentication created for user: {} with roles: {}", username, roles);
        return auth;
    }

    public long getAccessTokenValidityInMilliseconds(boolean isAdmin) {
        return isAdmin ? adminAccessTokenValidityInMilliseconds : defaultAccessTokenValidityInMilliseconds;
    }

    public long getRefreshTokenValidityInMilliseconds(boolean isAdmin) {
        return isAdmin ? adminRefreshTokenValidityInMilliseconds : defaultRefreshTokenValidityInMilliseconds;
    }

    public void invalidateToken(String token) {
        logger.debug("Invalidating token");
        tokenBlacklist.addToBlacklist(token);
        logger.info("Token invalidated and added to blacklist");
    }

    public String getEmailFromToken(String token) {
        return getUsername(token);
    }
}
