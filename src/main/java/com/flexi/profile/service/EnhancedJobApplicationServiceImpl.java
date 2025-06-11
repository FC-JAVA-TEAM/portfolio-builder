package com.flexi.profile.service;

import com.flexi.profile.dto.JobApplicationDTO;
import com.flexi.profile.model.JobApplication;
import com.flexi.profile.model.JobApplication.ApplicationStatus;
import com.flexi.profile.model.JobPosting;
import com.flexi.profile.model.User;
import com.flexi.profile.model.Role;
import com.flexi.profile.repository.JobApplicationRepository;
import com.flexi.profile.repository.JobPostingRepository;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.exception.ResourceNotFoundException;
import com.flexi.profile.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnhancedJobApplicationServiceImpl implements EnhancedJobApplicationService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public JobApplicationDTO submitApplication(Long jobId, Long userId, JobApplicationDTO applicationDTO) {
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        JobApplication application = new JobApplication();
        application.setJobPosting(jobPosting);
        application.setApplicant(user);
        application.setCoverLetter(applicationDTO.getCoverLetter());
        application.setResumeUrl(applicationDTO.getResumeUrl());
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setAppliedAt(LocalDateTime.now());

        JobApplication savedApplication = jobApplicationRepository.save(application);
        return convertToDTO(savedApplication);
    }

    @Override
    @Transactional
    public JobApplicationDTO updateApplicationStatus(Long id, ApplicationStatus status, Long userId) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!hasRole(user, "HR") && !hasRole(user, "ADMIN")) {
            throw new UnauthorizedException("User is not authorized to update application status");
        }

        application.setStatus(status);
        application.setUpdatedAt(LocalDateTime.now());

        JobApplication updatedApplication = jobApplicationRepository.save(application);
        return convertToDTO(updatedApplication);
    }

    @Override
    public List<JobApplicationDTO> getAllApplications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<JobApplication> applications;
        if (hasRole(user, "HR") || hasRole(user, "ADMIN")) {
            applications = jobApplicationRepository.findAll();
        } else {
            applications = jobApplicationRepository.findByApplicantId(userId);
        }

        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public JobApplicationDTO getApplicationById(Long id, Long userId) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!hasRole(user, "HR") && !hasRole(user, "ADMIN") && !application.getApplicant().getId().equals(userId)) {
            throw new UnauthorizedException("User is not authorized to view this application");
        }

        return convertToDTO(application);
    }

    @Override
    public List<JobApplicationDTO> getApplicationsByJobPosting(Long jobId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!hasRole(user, "HR") && !hasRole(user, "ADMIN")) {
            throw new UnauthorizedException("User is not authorized to view applications for this job posting");
        }

        List<JobApplication> applications = jobApplicationRepository.findByJobPostingId(jobId);
        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobApplicationDTO> getApplicationsByStatus(ApplicationStatus status, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<JobApplication> applications;
        if (hasRole(user, "HR") || hasRole(user, "ADMIN")) {
            applications = jobApplicationRepository.findByStatus(status);
        } else {
            applications = jobApplicationRepository.findByApplicantIdAndStatus(userId, status);
        }

        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private JobApplicationDTO convertToDTO(JobApplication application) {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setId(application.getId());
        dto.setJobId(application.getJobPosting().getId());
        dto.setUserId(application.getApplicant().getId());
        dto.setCoverLetter(application.getCoverLetter());
        dto.setResumeUrl(application.getResumeUrl());
        dto.setStatus(application.getStatus());
        dto.setAppliedAt(application.getAppliedAt());
        dto.setUpdatedAt(application.getUpdatedAt());
        dto.setApplicantName(application.getApplicant().getFirstName() + " " + application.getApplicant().getLastName());
        dto.setJobTitle(application.getJobPosting().getTitle());
        return dto;
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_" + roleName));
    }
}
