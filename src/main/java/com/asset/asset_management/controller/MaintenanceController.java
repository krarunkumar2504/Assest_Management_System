package com.asset.asset_management.controller;

import com.asset.asset_management.model.MaintenanceRecord;
import com.asset.asset_management.service.MaintenanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService service;

    public MaintenanceController(MaintenanceService service) {
        this.service = service;
    }

    // ── GET all maintenance records ───────────────────────────
    // Called by: Maintenance.jsx on page load
    // Returns: list of all rows from maintenance_records table
    @GetMapping
    public List<MaintenanceRecord> getAll() {
        return service.getAll();
    }

    // ── GET single record by ID ───────────────────────────────
    // Called by: Edit modal (optional — for pre-filling fresh from DB)
    // Returns: 200 with record, or 404 if not found
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRecord> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── POST create new maintenance record ────────────────────
    // Called by: "Schedule Maintenance" modal on submit
    // Body: { assetId, maintenanceDate, maintenanceType, cost,
    //         description, vendorName, performedBy, nextDueDate }
    // Returns: 200 with the saved record (including generated id)
    @PostMapping
    public MaintenanceRecord create(@RequestBody MaintenanceRecord record) {
        return service.create(record);
    }

    // ── PUT update existing maintenance record ────────────────
    // Called by: Edit modal on submit
    // Body: same fields as POST
    // Returns: 200 with updated record, or 404 if id not found
    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceRecord> update(
            @PathVariable Long id,
            @RequestBody MaintenanceRecord updated) {

        return service.update(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── DELETE maintenance record ─────────────────────────────
    // Called by: Delete confirmation dialog
    // Returns: 204 No Content on success, 404 if id not found
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = service.delete(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}