package com.flexi.profile.controller;

import com.flexi.profile.service.RefreshTokenService;
import com.flexi.profile.service.AuditService;
import com.flexi.profile.util.LogUtil;
import com.flexi.profile.exception.service.auth.TokenOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuditService auditService;

@PostMapping("/tokens/revoke/{userId}")
public ResponseEntity<?> revokeUserTokens(@PathVariable Long userId) {
    logger.debug("Received request to revoke all tokens for user: {}", userId);
    LogUtil.logMethodEntry(logger, "revokeUserTokens", userId);
    try {
        refreshTokenService.deleteByUserId(userId);
        ApiResponse response = new ApiResponse(true, "All tokens revoked for user: " + userId);
        logger.info("Successfully revoked all tokens for user: {}", userId);
        LogUtil.logMethodExit(logger, "revokeUserTokens", response);
        return ResponseEntity.ok().body(response);
    } catch (Exception e) {
        logger.error("Error revoking tokens for user: {}", userId, e);
        throw new TokenOperationException("Failed to revoke tokens for user: " + userId, e);
    }
}

@PostMapping("/tokens/revoke-all")
public ResponseEntity<?> revokeAllTokens() {
    logger.debug("Received request to revoke all tokens in the system");
    LogUtil.logMethodEntry(logger, "revokeAllTokens");
    try {
        // First, log this administrative action
        auditService.logTokenAction("REVOKE_ALL", 0L, null, "Admin revoked all tokens in the system");
        LogUtil.logInfo(logger, "Admin initiated revocation of all tokens");
        logger.debug("Initiating system-wide token revocation");
        
        // Get all active tokens and revoke them
        refreshTokenService.revokeAllTokens();
        
        ApiResponse response = new ApiResponse(true, "All tokens in the system have been revoked");
        logger.info("Successfully revoked all tokens in the system");
        LogUtil.logMethodExit(logger, "revokeAllTokens", response);
        return ResponseEntity.ok().body(response);
    } catch (Exception e) {
        logger.error("Error revoking all tokens", e);
        throw new TokenOperationException("Failed to revoke all tokens", e);
    }
}

@GetMapping("/tokens/cleanup")
public ResponseEntity<?> cleanupExpiredTokens() {
    logger.debug("Received request to cleanup expired tokens");
    LogUtil.logMethodEntry(logger, "cleanupExpiredTokens");
    try {
        refreshTokenService.cleanupExpiredTokens();
        ApiResponse response = new ApiResponse(true, "Expired tokens have been cleaned up");
        logger.info("Successfully completed expired tokens cleanup");
        LogUtil.logMethodExit(logger, "cleanupExpiredTokens", response);
        return ResponseEntity.ok().body(response);
    } catch (Exception e) {
        logger.error("Error cleaning up expired tokens", e);
        throw new TokenOperationException("Failed to clean up expired tokens", e);
    }
}
}

class ApiResponse {
    private boolean success;
    private String message;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
