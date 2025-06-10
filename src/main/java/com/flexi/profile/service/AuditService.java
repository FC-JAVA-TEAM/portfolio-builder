package com.flexi.profile.service;

import com.flexi.profile.model.AuditLog;
import com.flexi.profile.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

@Transactional
public void logTokenAction(String action, Long userId, Long tokenId, String details) {
    AuditLog log = new AuditLog();
    log.setAction(action);
    log.setUserId(userId);
    log.setTokenId(tokenId);
    log.setDetails(details);
    log.setTimestamp(Instant.now());
    auditLogRepository.save(log);
}
}
