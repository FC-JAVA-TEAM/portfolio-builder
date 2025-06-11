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
import com.flexi.profile.exception.jobapplication.*;
import com.flexi.profile.exception.jobposting.JobPostingNotFoundException;
import com.flexi.profile.exception.jobposting.UserNotFoundException;
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
                .orElseThrow(() -> new JobPostingNotFoundException(jobId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Validate application data
        validateApplicationData(applicationDTO);

        // Check if job posting is closed
        validateJobPostingStatus(jobPosting);

        // Check for duplicate application
        checkDuplicateApplication(userId, jobId);

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
                .orElseThrow(() -> new JobApplicationNotFoundException(id));

        // Check if user has access to update the application
        checkApplicationAccess(application, userId);

        // Validate the status transition
        validateStatusTransition(application.getStatus(), status);

        application.setStatus(status);
        application.setUpdatedAt(LocalDateTime.now());

        JobApplication updatedApplication = jobApplicationRepository.save(application);
        return convertToDTO(updatedApplication);
    }

    @Override
    public List<JobApplicationDTO> getAllApplications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

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
                .orElseThrow(() -> new JobApplicationNotFoundException(id));

        checkApplicationAccess(application, userId);

        return convertToDTO(application);
    }

    @Override
    public List<JobApplicationDTO> getApplicationsByJobPosting(Long jobId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new JobPostingNotFoundException(jobId));

        if (!hasRole(user, "HR") && !hasRole(user, "ADMIN")) {
            throw new UnauthorizedApplicationAccessException(userId, jobId, "view applications");
        }

        List<JobApplication> applications = jobApplicationRepository.findByJobPostingId(jobId);
        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobApplicationDTO> getApplicationsByStatus(ApplicationStatus status, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

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

    private void validateStatusTransition(ApplicationStatus currentStatus, ApplicationStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case SUBMITTED:
                if (newStatus != ApplicationStatus.UNDER_REVIEW) {
                    throw new InvalidApplicationStatusTransitionException(currentStatus, newStatus);
                }
                break;
            case UNDER_REVIEW:
                if (newStatus != ApplicationStatus.INTERVIEWED && newStatus != ApplicationStatus.REJECTED) {
                    throw new InvalidApplicationStatusTransitionException(currentStatus, newStatus);
                }
                break;
            case INTERVIEWED:
                if (newStatus != ApplicationStatus.ACCEPTED && newStatus != ApplicationStatus.REJECTED) {
                    throw new InvalidApplicationStatusTransitionException(currentStatus, newStatus);
                }
                break;
            case ACCEPTED:
            case REJECTED:
                throw new InvalidApplicationStatusTransitionException(currentStatus, newStatus);
            default:
                throw new InvalidApplicationStatusTransitionException(currentStatus, newStatus);
        }
    }

    private void validateApplicationData(JobApplicationDTO applicationDTO) {
        if (applicationDTO.getResumeUrl() == null || applicationDTO.getResumeUrl().trim().isEmpty()) {
            throw new ApplicationValidationException("resumeUrl", "Resume URL cannot be empty");
        }
        if (applicationDTO.getCoverLetter() == null || applicationDTO.getCoverLetter().trim().isEmpty()) {
            throw new ApplicationValidationException("coverLetter", "Cover letter cannot be empty");
        }
    }

    private void checkDuplicateApplication(Long userId, Long jobId) {
        if (jobApplicationRepository.existsByApplicantIdAndJobPostingId(userId, jobId)) {
            throw new DuplicateApplicationException(userId, jobId);
        }
    }

    private void validateJobPostingStatus(JobPosting jobPosting) {
        if (jobPosting.getStatus() == JobPosting.JobStatus.CLOSED) {
            throw new ClosedJobPostingException(jobPosting.getId());
        }
    }

    private void checkApplicationAccess(JobApplication application, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!hasRole(user, "HR") && !hasRole(user, "ADMIN") && 
            !application.getApplicant().getId().equals(userId)) {
            throw new UnauthorizedApplicationAccessException(userId, application.getId(), "access");
        }
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
