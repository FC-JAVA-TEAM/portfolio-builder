package com.flexi.profile.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "role_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role requestedRole;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(columnDefinition = "TEXT")
    private String justification;

    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    @ManyToOne
    @JoinColumn(name = "processed_by")
    private User processedBy;

    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
        status = RequestStatus.PENDING;
    }

    public void approve(User admin) {
        this.status = RequestStatus.APPROVED;
        this.processedBy = admin;
        this.processedAt = LocalDateTime.now();
    }

    public void reject(User admin) {
        this.status = RequestStatus.REJECTED;
        this.processedBy = admin;
        this.processedAt = LocalDateTime.now();
    }

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED
    }
}
