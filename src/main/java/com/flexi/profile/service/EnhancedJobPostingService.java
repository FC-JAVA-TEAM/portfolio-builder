package com.flexi.profile.service;

import com.flexi.profile.dto.JobPostingDTO;
import com.flexi.profile.model.JobPosting.JobStatus;

import java.util.List;

public interface EnhancedJobPostingService {
    JobPostingDTO createJobPosting(JobPostingDTO jobPosting, Long userId);
    JobPostingDTO updateJobPosting(Long id, JobPostingDTO jobPostingDetails, Long userId);
    void deleteJobPosting(Long id, Long userId);
    List<JobPostingDTO> getAllJobPostings();
    JobPostingDTO getJobPostingById(Long id);
    List<JobPostingDTO> getJobPostingsByStatus(JobStatus status);
    List<JobPostingDTO> getJobPostingsByDepartment(String department);
    List<JobPostingDTO> getJobPostingsByEmploymentType(String employmentType);
}
