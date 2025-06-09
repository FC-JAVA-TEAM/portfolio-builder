package com.flexi.profile.controller;

import com.flexi.profile.dto.RolePromotionResponse;
import com.flexi.profile.model.User;
import com.flexi.profile.service.RoleManagementService;
import com.flexi.profile.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleManagementService hrManagementService;

    @PutMapping("/{role}/promote")
    public ResponseEntity<RolePromotionResponse> promoteUserToRole(
            @PathVariable String role,
            @Valid @RequestBody User user, 
            Authentication authentication) {
        
        String adminEmail = authentication.getName();
        String targetRole = "ROLE_" + role.toUpperCase();
        
        logger.debug("Received request to promote user {} to {} by admin {}", user.getEmail(), role.toUpperCase(), adminEmail);
        LogUtil.logMethodEntry(logger, "promoteUserToRole", user.getEmail(), role.toUpperCase());

        try {
            RolePromotionResponse response = hrManagementService.promoteUserToRole(user, targetRole, adminEmail);
            
            logger.info("Successfully promoted user {} to {} role by admin {}", user.getEmail(), role.toUpperCase(), adminEmail);
            LogUtil.logMethodExit(logger, "promoteUserToRole", response);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error promoting user {} to {} role by admin {}", user.getEmail(), role.toUpperCase(), adminEmail, e);
            throw e;
        }
    }

    // Keep backward compatibility with the old HR endpoint
    @PutMapping("/hr/promote")
    public ResponseEntity<RolePromotionResponse> promoteUserToHR(
            @Valid @RequestBody User user, 
            Authentication authentication) {
        
        String adminEmail = authentication.getName();
        logger.debug("Received request to promote user {} to HR by admin {} (legacy endpoint)", user.getEmail(), adminEmail);
        LogUtil.logMethodEntry(logger, "promoteUserToHR", user.getEmail());

        try {
            RolePromotionResponse response = hrManagementService.promoteUserToHR(user, adminEmail);
            
            logger.info("Successfully promoted user {} to HR role by admin {} (legacy endpoint)", user.getEmail(), adminEmail);
            LogUtil.logMethodExit(logger, "promoteUserToHR", response);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error promoting user {} to HR role by admin {} (legacy endpoint)", user.getEmail(), adminEmail, e);
            throw e;
        }
    }
}
