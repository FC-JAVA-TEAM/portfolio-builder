package com.flexi.profile.repository;

import com.flexi.profile.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // You can add custom query methods here if needed
}
