package com.flexi.profile.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.flexi.profile.exception.UnauthorizedException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklist tokenBlacklist;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, TokenBlacklist tokenBlacklist) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);
        logger.debug("Request URL: {}", request.getRequestURL());
        logger.debug("Token: {}", (token != null ? "Present" : "Not present"));
        
        try {
            if (token != null) {
                logger.debug("Validating token...");
                
                // Check if token is blacklisted
                if (tokenBlacklist.isBlacklisted(token)) {
                    logger.info("Token is blacklisted");
                    SecurityContextHolder.clearContext();
                    throw new UnauthorizedException("Token has been invalidated");
                }
                
                boolean isValid = jwtTokenProvider.validateToken(token);
                logger.debug("Token validation result: {}", isValid);
                
                if (isValid) {
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.debug("Authentication set in SecurityContext");
                }
            }
        } catch (Exception ex) {
            logger.error("Error processing token: {}", ex.getMessage(), ex);
            SecurityContextHolder.clearContext();
            throw new UnauthorizedException("Invalid JWT token: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
