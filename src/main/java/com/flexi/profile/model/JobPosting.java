package com.flexi.profile.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "job_postings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String organization;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "job_required_skills", joinColumns = @JoinColumn(name = "job_posting_id"))
    @Column(name = "skill")
    private Set<String> skills;

    @Column
    private String locationsDerived;

    @Column
    private String type;

    @Column
    private LocalDate datePosted;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column
    private String source;

    @Column
    private String sourceDomain;

    @Column
    private int rating;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column
    private String jobLink;

    @Column
    private boolean applied;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime updatedAt;

    public enum JobStatus {
        OPEN, CLOSED, DRAFT
    }

    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
        datePosted = LocalDate.now();
        updatedAt = LocalDateTime.now();
        source = "Internal";
        rating = 0;
        applied = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
