package com.flexi.profile.controller;

import com.flexi.profile.dto.JobPostingDTO;
import com.flexi.profile.model.JobPosting.JobStatus;
import com.flexi.profile.model.User;
import com.flexi.profile.response.ApiResponse;
import com.flexi.profile.response.ResponseMessage;
import com.flexi.profile.service.EnhancedJobPostingService;
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
@RequestMapping("/api/v2/job-postings")
public class EnhancedJobPostingController {

    @Autowired
    private EnhancedJobPostingService jobPostingService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<JobPostingDTO>> createJobPosting(
            @RequestBody JobPostingDTO jobPosting,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobPostingDTO createdJobPosting = jobPostingService.createJobPosting(jobPosting, userId);
        
        ApiResponse<JobPostingDTO> response = ApiResponse.<JobPostingDTO>success()
            .message(ResponseMessage.JOB_POSTING_CREATED.getMessage())
            .data(createdJobPosting)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<JobPostingDTO>> updateJobPosting(
            @PathVariable Long id,
            @RequestBody JobPostingDTO jobPostingDetails,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobPostingDTO updatedJobPosting = jobPostingService.updateJobPosting(id, jobPostingDetails, userId);
        
        ApiResponse<JobPostingDTO> response = ApiResponse.<JobPostingDTO>success()
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
    public ResponseEntity<ApiResponse<List<JobPostingDTO>>> getAllJobPostings() {
        List<JobPostingDTO> jobPostings = jobPostingService.getAllJobPostings();
        
        ApiResponse<List<JobPostingDTO>> response = ApiResponse.<List<JobPostingDTO>>success()
            .message(ResponseMessage.JOB_POSTINGS_RETRIEVED.getMessage())
            .data(jobPostings)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobPostingDTO>> getJobPostingById(@PathVariable Long id) {
        JobPostingDTO jobPosting = jobPostingService.getJobPostingById(id);
        
        ApiResponse<JobPostingDTO> response = ApiResponse.<JobPostingDTO>success()
            .message(ResponseMessage.JOB_POSTING_RETRIEVED.getMessage())
            .data(jobPosting)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<JobPostingDTO>>> getJobPostingsByStatus(@PathVariable JobStatus status) {
        List<JobPostingDTO> jobPostings = jobPostingService.getJobPostingsByStatus(status);
        
        ApiResponse<List<JobPostingDTO>> response = ApiResponse.<List<JobPostingDTO>>success()
            .message(ResponseMessage.STATUS_JOB_POSTINGS_RETRIEVED.getMessage())
            .data(jobPostings)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<JobPostingDTO>>> getJobPostingsByDepartment(@PathVariable String department) {
        List<JobPostingDTO> jobPostings = jobPostingService.getJobPostingsByDepartment(department);
        
        ApiResponse<List<JobPostingDTO>> response = ApiResponse.<List<JobPostingDTO>>success()
            .message(ResponseMessage.DEPARTMENT_JOB_POSTINGS_RETRIEVED.getMessage())
            .data(jobPostings)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employment-type/{employmentType}")
    public ResponseEntity<ApiResponse<List<JobPostingDTO>>> getJobPostingsByEmploymentType(@PathVariable String employmentType) {
        List<JobPostingDTO> jobPostings = jobPostingService.getJobPostingsByEmploymentType(employmentType);
        
        ApiResponse<List<JobPostingDTO>> response = ApiResponse.<List<JobPostingDTO>>success()
            .message(ResponseMessage.EMPLOYMENT_TYPE_JOB_POSTINGS_RETRIEVED.getMessage())
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
        return userRepository.findByEmail(username)
            .map(User::getId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
