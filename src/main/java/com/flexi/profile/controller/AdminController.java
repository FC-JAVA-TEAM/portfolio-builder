package com.flexi.profile.controller;

import com.flexi.profile.service.RefreshTokenService;
import com.flexi.profile.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AdminController {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuditService auditService;

    @PostMapping("/tokens/revoke/{userId}")
    public ResponseEntity<?> revokeUserTokens(@PathVariable String userId) {
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok()
            .body(new ApiResponse(true, "All tokens revoked for user: " + userId));
    }

    @PostMapping("/tokens/revoke-all")
    public ResponseEntity<?> revokeAllTokens() {
        // First, log this administrative action
        auditService.logTokenAction("REVOKE_ALL", "ADMIN", null, "Admin revoked all tokens in the system");
        
        // Get all active tokens and revoke them
        refreshTokenService.revokeAllTokens();
        
        return ResponseEntity.ok()
            .body(new ApiResponse(true, "All tokens in the system have been revoked"));
    }

    @GetMapping("/tokens/cleanup")
    public ResponseEntity<?> cleanupExpiredTokens() {
        refreshTokenService.cleanupExpiredTokens();
        return ResponseEntity.ok()
            .body(new ApiResponse(true, "Expired tokens have been cleaned up"));
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
}
