package com.flexi.profile.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(columnDefinition = "TEXT")
    private String resumeUrl;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = ApplicationStatus.SUBMITTED;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ApplicationStatus {
        SUBMITTED, UNDER_REVIEW, INTERVIEWED, ACCEPTED, REJECTED
    }
}
