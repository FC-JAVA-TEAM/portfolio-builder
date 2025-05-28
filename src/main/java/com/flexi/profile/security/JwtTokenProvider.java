package com.flexi.profile.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

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

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(String username, boolean isAdmin) {
        long validity = isAdmin ? adminAccessTokenValidityInMilliseconds : defaultAccessTokenValidityInMilliseconds;
        return createToken(username, validity);
    }

    public String createRefreshToken(String username, boolean isAdmin) {
        long validity = isAdmin ? adminRefreshTokenValidityInMilliseconds : defaultRefreshTokenValidityInMilliseconds;
        return createToken(username, validity);
    }

    private String createToken(String username, long validityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(username);
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        return new UsernamePasswordAuthenticationToken(username, "", List.of());
    }

    public long getAccessTokenValidityInMilliseconds(boolean isAdmin) {
        return isAdmin ? adminAccessTokenValidityInMilliseconds : defaultAccessTokenValidityInMilliseconds;
    }

    public long getRefreshTokenValidityInMilliseconds(boolean isAdmin) {
        return isAdmin ? adminRefreshTokenValidityInMilliseconds : defaultRefreshTokenValidityInMilliseconds;
    }
}
