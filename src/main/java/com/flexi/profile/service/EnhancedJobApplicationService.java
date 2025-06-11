package com.flexi.profile.service;

import com.flexi.profile.dto.JobApplicationDTO;
import com.flexi.profile.model.JobApplication.ApplicationStatus;
import java.util.List;

public interface EnhancedJobApplicationService {
    /**
     * Submit a new job application
     */
    JobApplicationDTO submitApplication(Long jobId, Long userId, JobApplicationDTO application);

    /**
     * Update the status of an existing job application
     */
    JobApplicationDTO updateApplicationStatus(Long id, ApplicationStatus status, Long userId);

    /**
     * Get all applications for a user (if USER role) or all applications (if HR/ADMIN role)
     */
    List<JobApplicationDTO> getAllApplications(Long userId);

    /**
     * Get a specific application by ID
     */
    JobApplicationDTO getApplicationById(Long id, Long userId);

    /**
     * Get all applications for a specific job posting
     */
    List<JobApplicationDTO> getApplicationsByJobPosting(Long jobId, Long userId);

    /**
     * Get all applications with a specific status
     */
    List<JobApplicationDTO> getApplicationsByStatus(ApplicationStatus status, Long userId);
}
