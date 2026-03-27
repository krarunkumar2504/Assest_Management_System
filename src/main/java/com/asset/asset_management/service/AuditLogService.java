package com.asset.asset_management.service;

import com.asset.asset_management.model.AuditLog;
import com.asset.asset_management.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepo;

    public AuditLogService(AuditLogRepository auditLogRepo) {
        this.auditLogRepo = auditLogRepo;
    }

    /**
     * Save an audit entry.
     *
     * @param action      e.g. "CREATE_EMPLOYEE"
     * @param description e.g. "Admin Arun created employee Rahul (ID: 5)"
     * @param performedBy admin email or display name
     */
    public void saveLog(String action, String description, String performedBy) {
        AuditLog log = new AuditLog(action, description, performedBy);
        auditLogRepo.save(log);
    }

    /** Return all logs ordered newest-first. */
    public List<AuditLog> getAllLogs() {
        return auditLogRepo.findAllByOrderByTimestampDesc();
    }
}