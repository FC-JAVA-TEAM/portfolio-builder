package com.flexi.profile.repository;

import com.flexi.profile.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    
    List<RefreshToken> findByUserId(String userId);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.userId = ?1")
    void deleteByUserId(String userId);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < ?1")
    void deleteAllExpiredTokens(Instant now);
    
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.userId = ?1 AND rt.deviceInfo = ?2 AND rt.id != ?3")
    void revokeAllUserTokensOnDevice(String userId, String deviceInfo, Long exceptTokenId);
    
    List<RefreshToken> findByUserIdAndDeviceInfo(String userId, String deviceInfo);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.family = ?1")
    void revokeAllTokensInFamily(String family);
}
