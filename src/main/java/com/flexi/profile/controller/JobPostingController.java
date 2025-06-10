package com.flexi.profile.controller;

import com.flexi.profile.model.JobPosting;
import com.flexi.profile.model.User;
import com.flexi.profile.service.JobPostingService;
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
@RequestMapping("/api/job-postings")
public class JobPostingController {

    @Autowired
    private JobPostingService jobPostingService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<JobPosting> createJobPosting(@RequestBody JobPosting jobPosting, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobPosting createdJobPosting = jobPostingService.createJobPosting(jobPosting, userId);
        return ResponseEntity.ok(createdJobPosting);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<JobPosting> updateJobPosting(@PathVariable Long id, @RequestBody JobPosting jobPostingDetails, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JobPosting updatedJobPosting = jobPostingService.updateJobPosting(id, jobPostingDetails, userId);
        return ResponseEntity.ok(updatedJobPosting);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<?> deleteJobPosting(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        jobPostingService.deleteJobPosting(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<JobPosting>> getAllJobPostings() {
        List<JobPosting> jobPostings = jobPostingService.getAllJobPostings();
        return ResponseEntity.ok(jobPostings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPosting> getJobPostingById(@PathVariable Long id) {
        JobPosting jobPosting = jobPostingService.getJobPostingById(id);
        return ResponseEntity.ok(jobPosting);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobPosting>> getJobPostingsByStatus(@PathVariable JobPosting.JobStatus status) {
        List<JobPosting> jobPostings = jobPostingService.getJobPostingsByStatus(status);
        return ResponseEntity.ok(jobPostings);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<JobPosting>> getJobPostingsByDepartment(@PathVariable String department) {
        List<JobPosting> jobPostings = jobPostingService.getJobPostingsByDepartment(department);
        return ResponseEntity.ok(jobPostings);
    }

    @GetMapping("/employment-type/{employmentType}")
    public ResponseEntity<List<JobPosting>> getJobPostingsByEmploymentType(@PathVariable String employmentType) {
        List<JobPosting> jobPostings = jobPostingService.getJobPostingsByEmploymentType(employmentType);
        return ResponseEntity.ok(jobPostings);
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
