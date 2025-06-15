package com.flexi.profile.service;

import com.flexi.profile.dto.JobPostingCreateDTO;
import com.flexi.profile.dto.JobPostingResponseDTO;
import com.flexi.profile.exception.jobposting.JobPostingNotFoundException;
import com.flexi.profile.exception.UnauthorizedException;
import com.flexi.profile.mapper.JobPostingResponseMapper;
import com.flexi.profile.model.JobPosting;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.JobPostingRepository;
import com.flexi.profile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
public class EnhancedJobPostingResponseService {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobPostingResponseMapper jobPostingResponseMapper;

    public JobPostingResponseDTO createJobPosting(JobPostingResponseDTO responseDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Create a JobPosting from the response DTO
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle(responseDTO.getTitle());
        jobPosting.setDescription(responseDTO.getDescription());
        jobPosting.setLocationsDerived(responseDTO.getLocationsDerived());
        jobPosting.setType(responseDTO.getType());
        jobPosting.setSkills(new HashSet<>(responseDTO.getSkills()));
        jobPosting.setOrganization(responseDTO.getOrganization());
        jobPosting.setSourceDomain(responseDTO.getSourceDomain());
        jobPosting.setStatus(JobPosting.JobStatus.OPEN);
        jobPosting.setCreatedBy(user);

        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);
        return jobPostingResponseMapper.toJobPostingResponseDTO(savedJobPosting);
    }

    public JobPostingResponseDTO getJobPostingResponseById(Long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new JobPostingNotFoundException("Job posting not found with id: " + id));
        return jobPostingResponseMapper.toJobPostingResponseDTO(jobPosting);
    }

    public List<JobPostingResponseDTO> getAllJobPostingResponses() {
        List<JobPosting> jobPostings = jobPostingRepository.findAll();
        return jobPostings.stream()
                .map(jobPostingResponseMapper::toJobPostingResponseDTO)
                .collect(Collectors.toList());
    }

    public JobPostingResponseDTO updateJobPosting(Long id, JobPostingResponseDTO jobPostingDetails, Long userId) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new JobPostingNotFoundException("Job posting not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!jobPosting.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("User is not authorized to update this job posting");
        }

        jobPosting.setTitle(jobPostingDetails.getTitle());
        jobPosting.setDescription(jobPostingDetails.getDescription());
        jobPosting.setLocationsDerived(jobPostingDetails.getLocationsDerived());
        jobPosting.setType(jobPostingDetails.getType());
        jobPosting.setSkills(new HashSet<>(jobPostingDetails.getSkills()));
        jobPosting.setOrganization(jobPostingDetails.getOrganization());
        jobPosting.setSourceDomain(jobPostingDetails.getSourceDomain());
        jobPosting.setStatus(JobPosting.JobStatus.valueOf(jobPostingDetails.getStatus()));

        JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);
        return jobPostingResponseMapper.toJobPostingResponseDTO(updatedJobPosting);
    }
}
