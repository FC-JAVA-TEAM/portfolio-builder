package com.flexi.profile.controller;

import com.flexi.profile.dto.JobApplicationDTO;
import com.flexi.profile.model.JobApplication.ApplicationStatus;
import com.flexi.profile.model.User;
import com.flexi.profile.response.ApiResponse;
import com.flexi.profile.response.ResponseMessage;
import com.flexi.profile.service.EnhancedJobApplicationService;
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
@RequestMapping("/api/v2/job-applications")
public class EnhancedJobApplicationController {

    @Autowired
    private EnhancedJobApplicationService jobApplicationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/jobs/{jobId}/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<JobApplicationDTO>> submitApplication(
            @PathVariable Long jobId,
            @RequestBody JobApplicationDTO application,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobApplicationDTO submittedApplication = jobApplicationService.submitApplication(jobId, userId, application);
        
        ApiResponse<JobApplicationDTO> response = ApiResponse.<JobApplicationDTO>success()
            .message(ResponseMessage.APPLICATION_SUBMITTED.getMessage())
            .data(submittedApplication)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<JobApplicationDTO>> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody ApplicationStatus status,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobApplicationDTO updatedApplication = jobApplicationService.updateApplicationStatus(id, status, userId);
        
        ApiResponse<JobApplicationDTO> response = ApiResponse.<JobApplicationDTO>success()
            .message(ResponseMessage.APPLICATION_STATUS_UPDATED.getMessage())
            .data(updatedApplication)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobApplicationDTO>>> getAllApplications(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<JobApplicationDTO> applications = jobApplicationService.getAllApplications(userId);
        
        ApiResponse<List<JobApplicationDTO>> response = ApiResponse.<List<JobApplicationDTO>>success()
            .message(ResponseMessage.APPLICATIONS_RETRIEVED.getMessage())
            .data(applications)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobApplicationDTO>> getApplicationById(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobApplicationDTO application = jobApplicationService.getApplicationById(id, userId);
        
        ApiResponse<JobApplicationDTO> response = ApiResponse.<JobApplicationDTO>success()
            .message(ResponseMessage.APPLICATION_RETRIEVED.getMessage())
            .data(application)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<JobApplicationDTO>>> getApplicationsByJobPosting(
            @PathVariable Long jobId,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<JobApplicationDTO> applications = jobApplicationService.getApplicationsByJobPosting(jobId, userId);
        
        ApiResponse<List<JobApplicationDTO>> response = ApiResponse.<List<JobApplicationDTO>>success()
            .message(ResponseMessage.JOB_APPLICATIONS_RETRIEVED.getMessage())
            .data(applications)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<JobApplicationDTO>>> getApplicationsByStatus(
            @PathVariable ApplicationStatus status,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<JobApplicationDTO> applications = jobApplicationService.getApplicationsByStatus(status, userId);
        
        ApiResponse<List<JobApplicationDTO>> response = ApiResponse.<List<JobApplicationDTO>>success()
            .message(ResponseMessage.STATUS_APPLICATIONS_RETRIEVED.getMessage())
            .data(applications)
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
