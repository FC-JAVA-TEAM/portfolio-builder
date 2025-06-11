package com.flexi.profile.mapper;

import com.flexi.profile.dto.JobPostingDTO;
import com.flexi.profile.model.JobPosting;
import com.flexi.profile.model.User;
import org.springframework.stereotype.Component;

@Component
public class JobPostingMapper {
    
    public JobPostingDTO toDTO(JobPosting jobPosting) {
        if (jobPosting == null) {
            return null;
        }

        JobPostingDTO dto = new JobPostingDTO();
        dto.setId(jobPosting.getId());
        dto.setTitle(jobPosting.getTitle());
        dto.setDescription(jobPosting.getDescription());
        dto.setDepartment(jobPosting.getDepartment());
        dto.setLocation(jobPosting.getLocation());
        dto.setEmploymentType(jobPosting.getEmploymentType());
        dto.setRequiredSkills(jobPosting.getRequiredSkills());
        dto.setStatus(jobPosting.getStatus());
        dto.setCreatedBy(jobPosting.getCreatedBy() != null ? jobPosting.getCreatedBy().getId() : null);
        dto.setCreatedAt(jobPosting.getCreatedAt());
        dto.setUpdatedAt(jobPosting.getUpdatedAt());
        return dto;
    }

    public JobPosting toEntity(JobPostingDTO dto) {
        if (dto == null) {
            return null;
        }

        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle(dto.getTitle());
        jobPosting.setDescription(dto.getDescription());
        jobPosting.setDepartment(dto.getDepartment());
        jobPosting.setLocation(dto.getLocation());
        jobPosting.setEmploymentType(dto.getEmploymentType());
        jobPosting.setRequiredSkills(dto.getRequiredSkills());
        jobPosting.setStatus(dto.getStatus());
        return jobPosting;
    }

    public void updateEntityFromDTO(JobPostingDTO dto, JobPosting jobPosting) {
        if (dto == null || jobPosting == null) {
            return;
        }

        jobPosting.setTitle(dto.getTitle());
        jobPosting.setDescription(dto.getDescription());
        jobPosting.setDepartment(dto.getDepartment());
        jobPosting.setLocation(dto.getLocation());
        jobPosting.setEmploymentType(dto.getEmploymentType());
        jobPosting.setRequiredSkills(dto.getRequiredSkills());
        jobPosting.setStatus(dto.getStatus());
    }
}
