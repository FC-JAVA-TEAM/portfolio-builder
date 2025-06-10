package com.flexi.profile.controller;

import com.flexi.profile.model.RoleRequest;
import com.flexi.profile.service.RoleService;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.model.User;
import com.flexi.profile.exception.UnauthorizedException;
import com.flexi.profile.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RoleRequest> requestRole(
            @RequestParam Long roleId,
            @RequestParam String justification,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        RoleRequest roleRequest = roleService.requestRole(userId, roleId, justification);
        return ResponseEntity.ok(roleRequest);
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleRequest>> getPendingRequests() {
        List<RoleRequest> requests = roleService.getPendingRequests();
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/requests/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleRequest> approveRequest(
            @PathVariable Long id,
            Authentication authentication) {
        Long adminId = getUserIdFromAuthentication(authentication);
        RoleRequest approvedRequest = roleService.approveRoleRequest(id, adminId);
        return ResponseEntity.ok(approvedRequest);
    }

    @PutMapping("/requests/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleRequest> rejectRequest(
            @PathVariable Long id,
            Authentication authentication) {
        Long adminId = getUserIdFromAuthentication(authentication);
        RoleRequest rejectedRequest = roleService.rejectRoleRequest(id, adminId);
        return ResponseEntity.ok(rejectedRequest);
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RoleRequest>> getMyRequests(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<RoleRequest> requests = roleService.getUserRoleRequests(userId);
        return ResponseEntity.ok(requests);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException("Authentication required");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            // Since we're using email as username, we can find the user by email
            return userRepository.findByEmail(username)
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }
        
        throw new UnauthorizedException("Invalid authentication");
    }
}
