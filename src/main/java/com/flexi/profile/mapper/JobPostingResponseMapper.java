package com.flexi.profile.mapper;

import com.flexi.profile.dto.JobPostingCreateDTO;
import com.flexi.profile.dto.JobPostingResponseDTO;
import com.flexi.profile.model.JobPosting;
import com.flexi.profile.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class JobPostingResponseMapper {

    public JobPostingResponseDTO toJobPostingResponseDTO(JobPosting jobPosting) {
        JobPostingResponseDTO responseDTO = new JobPostingResponseDTO();

        // Generate a job ID in the format "job-XXX-YYYY" where XXX is a padded number and YYYY is the year
        String jobId = String.format("job-%03d-%d", 
            jobPosting.getId() != null ? jobPosting.getId() : 0, 
            jobPosting.getDateCreated().getYear());
        
        responseDTO.setId(jobId);
        responseDTO.setTitle(jobPosting.getTitle());
        responseDTO.setOrganization(jobPosting.getOrganization());
        responseDTO.setDescription(jobPosting.getDescription());
        responseDTO.setSkills(new ArrayList<>(jobPosting.getSkills()));
        responseDTO.setLocationsDerived(jobPosting.getLocationsDerived());
        responseDTO.setType(jobPosting.getType());
        responseDTO.setDatePosted(jobPosting.getDatePosted());
        responseDTO.setDateCreated(jobPosting.getDateCreated());
        responseDTO.setSource("Internal");
        responseDTO.setSourceDomain(jobPosting.getSourceDomain() != null ? 
            jobPosting.getSourceDomain() : "flexiprofile.com");
        responseDTO.setRating(jobPosting.getRating());
        responseDTO.setStatus(jobPosting.getStatus() != null ? 
            jobPosting.getStatus().toString() : "Open");
        responseDTO.setJob_link(String.format("https://%s/careers/%s", 
            jobPosting.getJobLink() != null ? jobPosting.getJobLink() : "flexiprofile.com",
            jobPosting.getTitle().toLowerCase().replace(" ", "-")));
        responseDTO.setApplied(false);

        return responseDTO;
    }

    public JobPosting toJobPosting(JobPostingCreateDTO createDTO, User createdBy) {
        JobPosting jobPosting = new JobPosting();
        
        jobPosting.setTitle(createDTO.getTitle());
        jobPosting.setDescription(createDTO.getDescription());
        jobPosting.setOrganization(createDTO.getOrganization());
        jobPosting.setLocationsDerived(createDTO.getLocationsDerived());
        jobPosting.setType(createDTO.getType());
        jobPosting.setSkills(createDTO.getSkills());
        jobPosting.setSourceDomain(createDTO.getSourceDomain());
        jobPosting.setStatus(JobPosting.JobStatus.OPEN);
        jobPosting.setCreatedBy(createdBy);

        return jobPosting;
    }
}
