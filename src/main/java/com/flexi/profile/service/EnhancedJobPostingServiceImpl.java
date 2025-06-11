package com.flexi.profile.service;

import com.flexi.profile.dto.JobPostingDTO;
import com.flexi.profile.model.JobPosting;
import com.flexi.profile.model.User;
import com.flexi.profile.model.JobPosting.JobStatus;
import com.flexi.profile.repository.JobPostingRepository;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.exception.jobposting.*;
import com.flexi.profile.mapper.JobPostingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnhancedJobPostingServiceImpl implements EnhancedJobPostingService {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobPostingMapper jobPostingMapper;

    @Override
    public JobPostingDTO createJobPosting(JobPostingDTO jobPostingDTO, Long userId) {
        // Validate job posting data
        validateJobPostingData(jobPostingDTO);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Check for duplicate job posting
        if (jobPostingRepository.existsByTitleAndDepartment(jobPostingDTO.getTitle(), jobPostingDTO.getDepartment())) {
            throw new DuplicateJobPostingException(jobPostingDTO.getTitle(), jobPostingDTO.getDepartment());
        }
        
        JobPosting jobPosting = jobPostingMapper.toEntity(jobPostingDTO);
        jobPosting.setCreatedBy(user);
        jobPosting.setStatus(JobStatus.DRAFT); // Set initial status to DRAFT
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);
        return convertToDTO(savedJobPosting);
    }

    @Override
    public JobPostingDTO updateJobPosting(Long id, JobPostingDTO jobPostingDetails, Long userId) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new JobPostingNotFoundException(id));

        if (!jobPosting.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedJobPostingActionException(userId, id, "update");
        }

        // If all fields are being updated, validate the complete data
        if (isCompleteUpdate(jobPostingDetails)) {
            validateJobPostingData(jobPostingDetails);
        }

        // Check if status is being updated
        if (jobPostingDetails.getStatus() != null && jobPostingDetails.getStatus() != jobPosting.getStatus()) {
            validateStatusTransition(jobPosting.getStatus(), jobPostingDetails.getStatus());
        }

        // Check for duplicate if title or department is being updated
        if ((jobPostingDetails.getTitle() != null && !jobPostingDetails.getTitle().equals(jobPosting.getTitle())) ||
            (jobPostingDetails.getDepartment() != null && !jobPostingDetails.getDepartment().equals(jobPosting.getDepartment()))) {
            if (jobPostingRepository.existsByTitleAndDepartment(
                    jobPostingDetails.getTitle() != null ? jobPostingDetails.getTitle() : jobPosting.getTitle(),
                    jobPostingDetails.getDepartment() != null ? jobPostingDetails.getDepartment() : jobPosting.getDepartment())) {
                throw new DuplicateJobPostingException(
                    jobPostingDetails.getTitle() != null ? jobPostingDetails.getTitle() : jobPosting.getTitle(),
                    jobPostingDetails.getDepartment() != null ? jobPostingDetails.getDepartment() : jobPosting.getDepartment());
            }
        }

        jobPostingMapper.updateEntityFromDTO(jobPostingDetails, jobPosting);
        JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);
        return convertToDTO(updatedJobPosting);
    }

    private void validateStatusTransition(JobStatus currentStatus, JobStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case DRAFT:
                if (newStatus != JobStatus.OPEN) {
                    throw new JobPostingStatusException(currentStatus, newStatus);
                }
                break;
            case OPEN:
                if (newStatus != JobStatus.CLOSED) {
                    throw new JobPostingStatusException(currentStatus, newStatus);
                }
                break;
            case CLOSED:
                throw new JobPostingStatusException(currentStatus, "update status");
            default:
                throw new JobPostingStatusException("Invalid current status: " + currentStatus);
        }
    }

    @Override
    public void deleteJobPosting(Long id, Long userId) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new JobPostingNotFoundException(id));

        if (!jobPosting.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedJobPostingActionException(userId, id, "delete");
        }

        // Prevent deletion of job postings that are in OPEN status
        if (jobPosting.getStatus() == JobStatus.OPEN) {
            throw new JobPostingStatusException(jobPosting.getStatus(), "delete");
        }

        jobPostingRepository.delete(jobPosting);
    }

    private void validateJobPostingData(JobPostingDTO jobPostingDTO) {
        if (jobPostingDTO.getTitle() == null || jobPostingDTO.getTitle().trim().isEmpty()) {
            throw new InvalidJobPostingDataException("title", "Title cannot be empty");
        }
        if (jobPostingDTO.getDepartment() == null || jobPostingDTO.getDepartment().trim().isEmpty()) {
            throw new InvalidJobPostingDataException("department", "Department cannot be empty");
        }
        if (jobPostingDTO.getDescription() == null || jobPostingDTO.getDescription().trim().isEmpty()) {
            throw new InvalidJobPostingDataException("description", "Description cannot be empty");
        }
        if (jobPostingDTO.getEmploymentType() == null || jobPostingDTO.getEmploymentType().trim().isEmpty()) {
            throw new InvalidJobPostingDataException("employmentType", "Employment type cannot be empty");
        }
        if (jobPostingDTO.getLocation() == null || jobPostingDTO.getLocation().trim().isEmpty()) {
            throw new InvalidJobPostingDataException("location", "Location cannot be empty");
        }
    }

    @Override
    public List<JobPostingDTO> getAllJobPostings() {
        return jobPostingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public JobPostingDTO getJobPostingById(Long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new JobPostingNotFoundException(id));
        return convertToDTO(jobPosting);
    }

    @Override
    public List<JobPostingDTO> getJobPostingsByStatus(JobStatus status) {
        return jobPostingRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobPostingDTO> getJobPostingsByDepartment(String department) {
        return jobPostingRepository.findByDepartment(department).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobPostingDTO> getJobPostingsByEmploymentType(String employmentType) {
        return jobPostingRepository.findByEmploymentType(employmentType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private JobPostingDTO convertToDTO(JobPosting jobPosting) {
        return jobPostingMapper.toDTO(jobPosting);
    }

    private boolean isCompleteUpdate(JobPostingDTO jobPostingDTO) {
        return jobPostingDTO.getTitle() != null &&
               jobPostingDTO.getDepartment() != null &&
               jobPostingDTO.getDescription() != null &&
               jobPostingDTO.getEmploymentType() != null &&
               jobPostingDTO.getLocation() != null &&
               jobPostingDTO.getStatus() != null;
    }
}
