package com.flexi.profile.service;

import com.flexi.profile.dto.JobPostingDTO;
import com.flexi.profile.model.JobPosting;
import com.flexi.profile.model.User;
import com.flexi.profile.model.JobPosting.JobStatus;
import com.flexi.profile.repository.JobPostingRepository;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.exception.ResourceNotFoundException;
import com.flexi.profile.exception.UnauthorizedException;
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
        JobPosting jobPosting = jobPostingMapper.toEntity(jobPostingDTO);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        jobPosting.setCreatedBy(user);
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);
        return convertToDTO(savedJobPosting);
    }

    @Override
    public JobPostingDTO updateJobPosting(Long id, JobPostingDTO jobPostingDetails, Long userId) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        if (!jobPosting.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this job posting");
        }

        jobPostingMapper.updateEntityFromDTO(jobPostingDetails, jobPosting);
        JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);
        return convertToDTO(updatedJobPosting);
    }

    @Override
    public void deleteJobPosting(Long id, Long userId) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        if (!jobPosting.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this job posting");
        }

        jobPostingRepository.delete(jobPosting);
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
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));
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
}
