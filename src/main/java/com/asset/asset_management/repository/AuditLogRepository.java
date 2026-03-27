package com.asset.asset_management.repository;

import com.asset.asset_management.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /** All logs sorted newest-first — used by GET /api/audit-logs */
    List<AuditLog> findAllByOrderByTimestampDesc();

    /** Logs since a given time — used for "today's actions" count */
    List<AuditLog> findByTimestampAfterOrderByTimestampDesc(LocalDateTime since);
}