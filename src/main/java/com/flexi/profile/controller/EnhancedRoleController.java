package com.flexi.profile.controller;

import com.flexi.profile.dto.RoleRequestDTO;
import com.flexi.profile.model.User;
import com.flexi.profile.response.ApiResponse;
import com.flexi.profile.response.ResponseMessage;
import com.flexi.profile.service.EnhancedRoleService;
import com.flexi.profile.exception.UnauthorizedException;
import com.flexi.profile.exception.ResourceNotFoundException;
import com.flexi.profile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/roles")
public class EnhancedRoleController {

    @Autowired
    private EnhancedRoleService roleService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<RoleRequestDTO>>> getMyRequests(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<RoleRequestDTO> requests = roleService.getUserRoleRequests(userId);
        
        ApiResponse<List<RoleRequestDTO>> response = ApiResponse.<List<RoleRequestDTO>>success()
            .message(ResponseMessage.USER_REQUESTS_RETRIEVED.getMessage())
            .data(requests)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @PostMapping("/request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<RoleRequestDTO>> requestRole(
            @RequestBody RoleRequestDTO roleRequestDTO,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        RoleRequestDTO roleRequest = roleService.requestRole(userId, roleRequestDTO.getRoleId(), roleRequestDTO.getJustification());
        
        ApiResponse<RoleRequestDTO> response = ApiResponse.<RoleRequestDTO>success()
            .message(ResponseMessage.ROLE_REQUEST_CREATED.getMessage())
            .data(roleRequest)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RoleRequestDTO>>> getPendingRequests() {
        List<RoleRequestDTO> requests = roleService.getPendingRequests();
        
        ApiResponse<List<RoleRequestDTO>> response = ApiResponse.<List<RoleRequestDTO>>success()
            .message(ResponseMessage.PENDING_REQUESTS_RETRIEVED.getMessage())
            .data(requests)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @PutMapping("/requests/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleRequestDTO>> approveRequest(
            @PathVariable Long id,
            Authentication authentication) {
        Long adminId = getUserIdFromAuthentication(authentication);
        RoleRequestDTO approvedRequest = roleService.approveRoleRequest(id, adminId);
        
        ApiResponse<RoleRequestDTO> response = ApiResponse.<RoleRequestDTO>success()
            .message(ResponseMessage.ROLE_REQUEST_APPROVED.getMessage())
            .data(approvedRequest)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @PutMapping("/requests/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleRequestDTO>> rejectRequest(
            @PathVariable Long id,
            Authentication authentication) {
        Long adminId = getUserIdFromAuthentication(authentication);
        RoleRequestDTO rejectedRequest = roleService.rejectRoleRequest(id, adminId);
        
        ApiResponse<RoleRequestDTO> response = ApiResponse.<RoleRequestDTO>success()
            .message(ResponseMessage.ROLE_REQUEST_REJECTED.getMessage())
            .data(rejectedRequest)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException("Authentication required");
        }
        
        String username = authentication.getName();
        return userRepository.findByEmail(username)
            .map(User::getId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
