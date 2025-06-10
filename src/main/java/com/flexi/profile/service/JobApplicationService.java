package com.flexi.profile.service;

import com.flexi.profile.model.JobApplication;
import com.flexi.profile.model.JobPosting;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.JobApplicationRepository;
import com.flexi.profile.repository.JobPostingRepository;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.exception.ResourceNotFoundException;
import com.flexi.profile.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JobApplicationService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UserRepository userRepository;

    public JobApplication submitApplication(Long jobPostingId, Long userId, JobApplication application) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (jobPosting.getStatus() != JobPosting.JobStatus.OPEN) {
            throw new UnauthorizedException("This job posting is no longer accepting applications");
        }

        if (jobApplicationRepository.existsByJobPostingIdAndApplicantId(jobPostingId, userId)) {
            throw new UnauthorizedException("You have already applied for this position");
        }

        application.setJobPosting(jobPosting);
        application.setApplicant(user);
        application.setStatus(JobApplication.ApplicationStatus.SUBMITTED);

        return jobApplicationRepository.save(application);
    }

    public JobApplication updateApplicationStatus(Long applicationId, JobApplication.ApplicationStatus status, Long userId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_HR") || role.getName().equals("ROLE_ADMIN"))) {
            throw new UnauthorizedException("User does not have permission to update application status");
        }

        application.setStatus(status);
        return jobApplicationRepository.save(application);
    }

    public List<JobApplication> getAllApplications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_HR") || role.getName().equals("ROLE_ADMIN"))) {
            return jobApplicationRepository.findAll();
        }

        return jobApplicationRepository.findByApplicantId(userId);
    }

    public JobApplication getApplicationById(Long applicationId, Long userId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isHrOrAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_HR") || role.getName().equals("ROLE_ADMIN"));

        if (!isHrOrAdmin && !application.getApplicant().getId().equals(userId)) {
            throw new UnauthorizedException("User does not have permission to view this application");
        }

        return application;
    }

    public List<JobApplication> getApplicationsByJobPosting(Long jobPostingId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_HR") || role.getName().equals("ROLE_ADMIN"))) {
            throw new UnauthorizedException("User does not have permission to view all applications");
        }

        return jobApplicationRepository.findByJobPostingId(jobPostingId);
    }

    public List<JobApplication> getApplicationsByStatus(JobApplication.ApplicationStatus status, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_HR") || role.getName().equals("ROLE_ADMIN"))) {
            return jobApplicationRepository.findByStatus(status);
        }

        return jobApplicationRepository.findByApplicantIdAndStatus(userId, status);
    }
}
