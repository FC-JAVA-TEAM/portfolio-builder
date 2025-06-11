package com.flexi.profile.repository;

import com.flexi.profile.model.JobApplication;
import com.flexi.profile.model.JobApplication.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByApplicantId(Long applicantId);
    List<JobApplication> findByJobPostingId(Long jobPostingId);
    List<JobApplication> findByStatus(ApplicationStatus status);
    List<JobApplication> findByApplicantIdAndStatus(Long applicantId, ApplicationStatus status);
    boolean existsByApplicantIdAndJobPostingId(Long applicantId, Long jobPostingId);
    boolean existsByJobPostingIdAndApplicantId(Long jobPostingId, Long applicantId);
}
