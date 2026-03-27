package com.asset.asset_management.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;          // e.g. "CREATE_EMPLOYEE", "DELETE_ASSET"

    @Column(columnDefinition = "TEXT")
    private String description;     // human-readable message

    @Column(name = "performed_by")
    private String performedBy;     // admin email or name

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // ── Lifecycle ──────────────────────────────────────────
    @PrePersist
    public void prePersist() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    // ── Constructors ───────────────────────────────────────
    public AuditLog() {}

    public AuditLog(String action, String description, String performedBy) {
        this.action      = action;
        this.description = description;
        this.performedBy = performedBy;
        this.timestamp   = LocalDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────
    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public String getAction()                  { return action; }
    public void setAction(String action)       { this.action = action; }

    public String getDescription()             { return description; }
    public void setDescription(String d)       { this.description = d; }

    public String getPerformedBy()             { return performedBy; }
    public void setPerformedBy(String p)       { this.performedBy = p; }

    public LocalDateTime getTimestamp()        { return timestamp; }
    public void setTimestamp(LocalDateTime t)  { this.timestamp = t; }
}
