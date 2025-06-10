package com.flexi.profile.repository;

import com.flexi.profile.model.JobPosting;
import com.flexi.profile.model.JobPosting.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findByStatus(JobStatus status);
    List<JobPosting> findByDepartment(String department);
    List<JobPosting> findByEmploymentType(String employmentType);
    List<JobPosting> findByCreatedById(Long userId);
}
