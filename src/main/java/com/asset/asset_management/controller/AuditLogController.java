package com.asset.asset_management.controller;

import com.asset.asset_management.model.AuditLog;
import com.asset.asset_management.service.AuditLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://asset-frontend-xi.vercel.app"
})
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * GET /api/audit-logs
     * Returns all audit logs sorted by timestamp descending (newest first).
     */
    @GetMapping("/audit-logs")
    public List<AuditLog> getAuditLogs() {
        return auditLogService.getAllLogs();
    }
}