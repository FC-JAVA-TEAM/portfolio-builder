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
        dto.setOrganization(jobPosting.getOrganization());
        dto.setDescription(jobPosting.getDescription());
        dto.setSkills(jobPosting.getSkills());
        dto.setLocationsDerived(jobPosting.getLocationsDerived());
        dto.setType(jobPosting.getType());
        dto.setDatePosted(jobPosting.getDatePosted());
        dto.setDateCreated(jobPosting.getDateCreated());
        dto.setSource(jobPosting.getSource());
        dto.setSourceDomain(jobPosting.getSourceDomain());
        dto.setRating(jobPosting.getRating());
        dto.setStatus(jobPosting.getStatus());
        dto.setJobLink(jobPosting.getJobLink());
        dto.setApplied(jobPosting.isApplied());
        dto.setCreatedBy(jobPosting.getCreatedBy() != null ? jobPosting.getCreatedBy().getId() : null);
        dto.setUpdatedAt(jobPosting.getUpdatedAt());
        return dto;
    }

    public JobPosting toEntity(JobPostingDTO dto) {
        if (dto == null) {
            return null;
        }

        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle(dto.getTitle());
        jobPosting.setOrganization(dto.getOrganization());
        jobPosting.setDescription(dto.getDescription());
        jobPosting.setSkills(dto.getSkills());
        jobPosting.setLocationsDerived(dto.getLocationsDerived());
        jobPosting.setType(dto.getType());
        jobPosting.setDatePosted(dto.getDatePosted());
        jobPosting.setDateCreated(dto.getDateCreated());
        jobPosting.setSource(dto.getSource());
        jobPosting.setSourceDomain(dto.getSourceDomain());
        jobPosting.setRating(dto.getRating());
        jobPosting.setStatus(dto.getStatus());
        jobPosting.setJobLink(dto.getJobLink());
        jobPosting.setApplied(dto.isApplied());
        return jobPosting;
    }

    public void updateEntityFromDTO(JobPostingDTO dto, JobPosting jobPosting) {
        if (dto == null || jobPosting == null) {
            return;
        }

        jobPosting.setTitle(dto.getTitle());
        jobPosting.setOrganization(dto.getOrganization());
        jobPosting.setDescription(dto.getDescription());
        jobPosting.setSkills(dto.getSkills());
        jobPosting.setLocationsDerived(dto.getLocationsDerived());
        jobPosting.setType(dto.getType());
        jobPosting.setDatePosted(dto.getDatePosted());
        jobPosting.setDateCreated(dto.getDateCreated());
        jobPosting.setSource(dto.getSource());
        jobPosting.setSourceDomain(dto.getSourceDomain());
        jobPosting.setRating(dto.getRating());
        jobPosting.setStatus(dto.getStatus());
        jobPosting.setJobLink(dto.getJobLink());
        jobPosting.setApplied(dto.isApplied());
    }
}
