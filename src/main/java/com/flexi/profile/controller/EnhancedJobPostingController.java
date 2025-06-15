package com.flexi.profile.controller;

import com.flexi.profile.dto.JobPostingCreateDTO;
import com.flexi.profile.dto.JobPostingDTO;
import com.flexi.profile.dto.JobPostingResponseDTO;
import com.flexi.profile.model.JobPosting.JobStatus;
import com.flexi.profile.model.User;
import com.flexi.profile.response.ApiResponse;
import com.flexi.profile.response.ResponseMessage;
import com.flexi.profile.service.EnhancedJobPostingResponseService;
import com.flexi.profile.service.EnhancedJobPostingService;
import com.flexi.profile.exception.UnauthorizedException;
import com.flexi.profile.exception.ResourceNotFoundException;
import com.flexi.profile.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/job-postings")
public class EnhancedJobPostingController {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedJobPostingController.class);

    @Autowired
    private EnhancedJobPostingService jobPostingService;

    @Autowired
    private UserRepository userRepository;
    
    
    @Autowired
    private EnhancedJobPostingResponseService enhancedJobPostingResponseService;

   
    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<JobPostingResponseDTO>> createJobPostingAI(
            @Valid @RequestBody JobPostingResponseDTO jobPostingDTO,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobPostingResponseDTO createdJobPosting = enhancedJobPostingResponseService.createJobPosting(jobPostingDTO, userId);
        
        ApiResponse<JobPostingResponseDTO> response = ApiResponse.<JobPostingResponseDTO>success()
            .message("Job posting created successfully")
            .data(createdJobPosting)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @PostMapping("/post")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<JobPostingResponseDTO>> createJobPosting(
            @Valid @RequestBody JobPostingResponseDTO jobPosting,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobPostingResponseDTO createdJobPosting = enhancedJobPostingResponseService.createJobPosting(jobPosting, userId);
        
        ApiResponse<JobPostingResponseDTO> response = ApiResponse.<JobPostingResponseDTO>success()
            .message(ResponseMessage.JOB_POSTING_CREATED.getMessage())
            .data(createdJobPosting)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<JobPostingResponseDTO>> updateJobPosting(
            @PathVariable Long id,
            @Valid @RequestBody JobPostingResponseDTO jobPostingDetails,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobPostingResponseDTO updatedJobPosting = enhancedJobPostingResponseService.updateJobPosting(id, jobPostingDetails, userId);
        
        ApiResponse<JobPostingResponseDTO> response = ApiResponse.<JobPostingResponseDTO>success()
            .message(ResponseMessage.JOB_POSTING_UPDATED.getMessage())
            .data(updatedJobPosting)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteJobPosting(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        jobPostingService.deleteJobPosting(id, userId);
        
        ApiResponse<Void> response = ApiResponse.<Void>success()
            .message(ResponseMessage.JOB_POSTING_DELETED.getMessage())
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobPostingResponseDTO>>> getAllJobPostings() {
        List<JobPostingResponseDTO> jobPostings = enhancedJobPostingResponseService.getAllJobPostingResponses();
        
        ApiResponse<List<JobPostingResponseDTO>> response = ApiResponse.<List<JobPostingResponseDTO>>success()
            .message(ResponseMessage.JOB_POSTINGS_RETRIEVED.getMessage())
            .data(jobPostings)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobPostingResponseDTO>> getJobPostingById(@PathVariable Long id) {
        JobPostingResponseDTO jobPosting = enhancedJobPostingResponseService.getJobPostingResponseById(id);
        
        ApiResponse<JobPostingResponseDTO> response = ApiResponse.<JobPostingResponseDTO>success()
            .message(ResponseMessage.JOB_POSTING_RETRIEVED.getMessage())
            .data(jobPosting)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<JobPostingResponseDTO>>> getJobPostingsByStatus(@PathVariable JobStatus status) {
        List<JobPostingResponseDTO> jobPostings = enhancedJobPostingResponseService.getAllJobPostingResponses().stream()
            .filter(jp -> jp.getStatus().equals(status.toString()))
            .collect(Collectors.toList());
        
        ApiResponse<List<JobPostingResponseDTO>> response = ApiResponse.<List<JobPostingResponseDTO>>success()
            .message(ResponseMessage.STATUS_JOB_POSTINGS_RETRIEVED.getMessage())
            .data(jobPostings)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/organization/{organization}")
    public ResponseEntity<ApiResponse<List<JobPostingResponseDTO>>> getJobPostingsByOrganization(@PathVariable String organization) {
        List<JobPostingResponseDTO> jobPostings = enhancedJobPostingResponseService.getAllJobPostingResponses().stream()
            .filter(jp -> jp.getOrganization().equalsIgnoreCase(organization))
            .collect(Collectors.toList());
        
        ApiResponse<List<JobPostingResponseDTO>> response = ApiResponse.<List<JobPostingResponseDTO>>success()
            .message(ResponseMessage.ORGANIZATION_JOB_POSTINGS_RETRIEVED.getMessage())
            .data(jobPostings)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<JobPostingResponseDTO>>> getJobPostingsByType(@PathVariable String type) {
        List<JobPostingResponseDTO> jobPostings = enhancedJobPostingResponseService.getAllJobPostingResponses().stream()
            .filter(jp -> jp.getType().equalsIgnoreCase(type))
            .collect(Collectors.toList());
        
        ApiResponse<List<JobPostingResponseDTO>> response = ApiResponse.<List<JobPostingResponseDTO>>success()
            .message(ResponseMessage.TYPE_JOB_POSTINGS_RETRIEVED.getMessage())
            .data(jobPostings)
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
        logger.debug("Getting user ID for username: {}", username);
        logger.debug("Authentication principal: {}", authentication.getPrincipal());
        logger.debug("Authentication authorities: {}", authentication.getAuthorities());
        
        return userRepository.findByEmail(username)
            .map(User::getId)
            .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}
