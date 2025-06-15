package com.flexi.profile.repository;

import com.flexi.profile.model.JobPosting;
import com.flexi.profile.model.JobPosting.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findByStatus(JobStatus status);
    List<JobPosting> findByOrganization(String organization);
    List<JobPosting> findByType(String type);
    List<JobPosting> findByCreatedById(Long userId);
    boolean existsByTitleAndOrganization(String title, String organization);
    
    // New methods
    List<JobPosting> findByLocationsDerivedContaining(String location);
    List<JobPosting> findBySkillsContaining(String skill);
    List<JobPosting> findByDatePostedAfter(LocalDate date);
}
