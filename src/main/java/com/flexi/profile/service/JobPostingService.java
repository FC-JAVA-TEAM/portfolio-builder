package com.flexi.profile.service;

import com.flexi.profile.model.JobPosting;
import com.flexi.profile.model.User;
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
public class JobPostingService {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UserRepository userRepository;

    public JobPosting createJobPosting(JobPosting jobPosting, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_HR") || role.getName().equals("ROLE_ADMIN"))) {
            throw new UnauthorizedException("User does not have permission to create job postings");
        }

        jobPosting.setCreatedBy(user);
        jobPosting.setStatus(JobPosting.JobStatus.OPEN);
        return jobPostingRepository.save(jobPosting);
    }

    public JobPosting updateJobPosting(Long id, JobPosting jobPostingDetails, Long userId) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_HR") || role.getName().equals("ROLE_ADMIN"))) {
            throw new UnauthorizedException("User does not have permission to update job postings");
        }

        jobPosting.setTitle(jobPostingDetails.getTitle());
        jobPosting.setDescription(jobPostingDetails.getDescription());
        jobPosting.setDepartment(jobPostingDetails.getDepartment());
        jobPosting.setEmploymentType(jobPostingDetails.getEmploymentType());
        jobPosting.setLocation(jobPostingDetails.getLocation());
        jobPosting.setRequiredSkills(jobPostingDetails.getRequiredSkills());
        jobPosting.setStatus(jobPostingDetails.getStatus());

        return jobPostingRepository.save(jobPosting);
    }

    public void deleteJobPosting(Long id, Long userId) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_HR") || role.getName().equals("ROLE_ADMIN"))) {
            throw new UnauthorizedException("User does not have permission to delete job postings");
        }

        jobPostingRepository.delete(jobPosting);
    }

    public List<JobPosting> getAllJobPostings() {
        return jobPostingRepository.findAll();
    }

    public JobPosting getJobPostingById(Long id) {
        return jobPostingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));
    }

    public List<JobPosting> getJobPostingsByStatus(JobPosting.JobStatus status) {
        return jobPostingRepository.findByStatus(status);
    }

    public List<JobPosting> getJobPostingsByDepartment(String department) {
        return jobPostingRepository.findByDepartment(department);
    }

    public List<JobPosting> getJobPostingsByEmploymentType(String employmentType) {
        return jobPostingRepository.findByEmploymentType(employmentType);
    }
}
