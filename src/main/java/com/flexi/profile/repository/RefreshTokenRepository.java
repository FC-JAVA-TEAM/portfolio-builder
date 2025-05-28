package com.flexi.profile.repository;

import com.flexi.profile.model.RefreshToken;
import com.flexi.profile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = ?1")
    List<RefreshToken> findByUser(User user);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = ?1")
    void deleteByUser(User user);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < ?1")
    void deleteAllExpiredTokens(Instant now);
    
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user AND rt.deviceInfo = :deviceInfo AND rt.id != :tokenId")
    void revokeAllUserTokensOnDevice(@Param("user") User user, @Param("deviceInfo") String deviceInfo, @Param("tokenId") Long tokenId);
    
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = ?1 AND rt.deviceInfo = ?2")
    List<RefreshToken> findByUserAndDeviceInfo(User user, String deviceInfo);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.family = ?1")
    void revokeAllTokensInFamily(String family);
}
