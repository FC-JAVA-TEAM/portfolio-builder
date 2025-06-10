package com.flexi.profile.controller;

import com.flexi.profile.model.JobApplication;
import com.flexi.profile.model.User;
import com.flexi.profile.service.JobApplicationService;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.exception.UnauthorizedException;
import com.flexi.profile.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationController {

    @Autowired
    private JobApplicationService jobApplicationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/jobs/{jobId}/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<JobApplication> submitApplication(
            @PathVariable Long jobId,
            @RequestBody JobApplication application,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobApplication submittedApplication = jobApplicationService.submitApplication(jobId, userId, application);
        return ResponseEntity.ok(submittedApplication);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<JobApplication> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam JobApplication.ApplicationStatus status,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobApplication updatedApplication = jobApplicationService.updateApplicationStatus(id, status, userId);
        return ResponseEntity.ok(updatedApplication);
    }

    @GetMapping
    public ResponseEntity<List<JobApplication>> getAllApplications(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<JobApplication> applications = jobApplicationService.getAllApplications(userId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplication> getApplicationById(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobApplication application = jobApplicationService.getApplicationById(id, userId);
        return ResponseEntity.ok(application);
    }

    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<List<JobApplication>> getApplicationsByJobPosting(
            @PathVariable Long jobId,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<JobApplication> applications = jobApplicationService.getApplicationsByJobPosting(jobId, userId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobApplication>> getApplicationsByStatus(
            @PathVariable JobApplication.ApplicationStatus status,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<JobApplication> applications = jobApplicationService.getApplicationsByStatus(status, userId);
        return ResponseEntity.ok(applications);
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
