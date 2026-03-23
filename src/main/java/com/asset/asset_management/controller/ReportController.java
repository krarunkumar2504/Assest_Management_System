package com.asset.asset_management.controller;


import com.asset.asset_management.repository.ReportRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportRepository repo;

    public ReportController(ReportRepository repo) {
        this.repo = repo;
    }

    // High Maintenance
    @GetMapping("/high-maintenance")
    public List<Object[]> highMaintenance() {
        return repo.getHighMaintenanceAssets();
    }

    // Department Summary
    @GetMapping("/department-summary")
    public List<Object[]> departmentSummary() {
        return repo.getDepartmentSummary();
    }

    // Upcoming Maintenance
    @GetMapping("/upcoming-maintenance")
    public List<Object[]> upcomingMaintenance() {
        return repo.getUpcomingMaintenance();
    }
}